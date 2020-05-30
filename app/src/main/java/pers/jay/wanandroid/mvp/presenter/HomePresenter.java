package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import okhttp3.ResponseBody;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.CollectHelper;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.http.RetryWithDelay;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.PoemUtils;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.ObjectUtils;
import pers.zjc.commonlibs.util.StringUtils;
import timber.log.Timber;

@FragmentScope
public class HomePresenter extends BasePresenter<HomeContract.Model, HomeContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    HomePresenter(HomeContract.Model model, HomeContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void requestArticle(int page) {
        mModel.getArticle(page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(1000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo info = response.getData();
                      mRootView.showMoreArticles(info);
                  }

                  @Override
                  public void onError(Throwable e) {
                      super.onError(e);
                      mRootView.showLoadMoreFail();
                  }

                  @Override
                  protected void onException(ExceptionReason reason) {
                      super.onException(reason);
                      mRootView.showLoadMoreFail();
                  }

                  @Override
                  public void onComplete() {
                      super.onComplete();
                  }
              });

    }

    /**
     * 请求轮播图
     */
    private void requestBanner() {
        mModel.getBanner()
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<List<BannerImg>>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<List<BannerImg>> response) {
                      List<BannerImg> bannerImgs = response.getData();
                      mRootView.showBanner(bannerImgs);
                  }

                  @Override
                  public void onComplete() {
                      mRootView.hideLoading();
                  }
              });
    }

    public void requestHomeData() {
        //使用zip合并首页三个创建网络访问的observable
        Observable.zip(mModel.getBanner(), mModel.getTopArticles(), mModel.getArticle(0),
                (bannerResponse, topResponse, commonResponse) -> {
                    List<Article> topArticles = topResponse.getData();
                    for (Article article : topArticles) {
                        article.setTop(true);
                    }
                    List<Article> articleList = commonResponse.getData().getDatas();
                    topArticles.addAll(articleList);
                    WanAndroidResponse<List<Article>> articleResponse = new WanAndroidResponse<>();
                    articleResponse.setData(topArticles);
                    WanAndroidResponse<ZipEntity> wanAndroidResponse = new WanAndroidResponse<>();
                    wanAndroidResponse.setData(new ZipEntity(bannerResponse, articleResponse));
                    return wanAndroidResponse;
                })
                  .compose(RxScheduler.Obs_io_main())
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .retryWhen(new RetryWithDelay(10, 2000))
                  .doOnNext(response -> {
                      ZipEntity zipEntity = response.getData();
                      List<BannerImg> bannerImgs = zipEntity.getBannerResponse().getData();
                      List<Article> articles = zipEntity.getArticleResponse().getData();
                      if (ObjectUtils.isNotEmpty(bannerImgs)) {
                          mRootView.showBanner(bannerImgs);
                      }
                      if (ObjectUtils.isNotEmpty(articles)) {
                          mRootView.refresh(articles);
                      }
                  })
                  .doOnComplete(() -> mRootView.hideLoading())
                  // 回到io线程发起下一个请求
                  .observeOn(Schedulers.io())
                  // 使用flatMap进行嵌套请求，完成以上三个网络请求后再请求每日一图
                  // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                  // 这里需要注意的是, flatMap并不保证事件的顺序,如果需要保证顺序则需要使用concatMap
                  .flatMap(
                          new Function<WanAndroidResponse<ZipEntity>, ObservableSource<ResponseBody>>() {
                              @Override
                              public ObservableSource<ResponseBody> apply(
                                      WanAndroidResponse<ZipEntity> zipEntityWanAndroidResponse) throws Exception {
                                  // 判断本地是否获取到，若无则获取今日诗词并保存本地
                                  String poem = AppConfig.getInstance().getPoem();
                                  String appName = JApplication.getInstance().getString(R.string.app_name);
                                  if (StringUtils.isEmpty(poem) || StringUtils.equals(poem, appName)) {
                                      PoemUtils.getPoemSync();
                                  }
                                  // 获取每日一图
                                  return mModel.getBingImg();
                              }
                          })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new ResourceObserver<ResponseBody>() {
                      @Override
                      protected void onStart() {
                          if (!NetWorkManager.isNetWorkAvailable()) {
                              mRootView.showNoNetwork();
                              dispose();
                          }
                      }

                      @Override
                      public void onNext(ResponseBody responseBody) {
                          String url = null;
                          try {
                              url = responseBody.string();
                          }
                          catch (IOException e) {
                              e.printStackTrace();
                          }
                          mRootView.addDailyPic(url);
                      }

                      @Override
                      public void onError(Throwable e) {

                      }

                      @Override
                      public void onComplete() {
                          mRootView.hideLoading();
                      }
                  });
    }

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        CollectHelper.with(mRootView).target(article).position(position).collect();
    }

    public void loadDailyPic() {
        mModel.getBingImg()
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new Observer<ResponseBody>() {
                  @Override
                  public void onSubscribe(Disposable d) {

                  }

                  @Override
                  public void onNext(ResponseBody responseBody) {
                      try {
                          String url = responseBody.string();
                          mRootView.addDailyPic(url);
                      }
                      catch (IOException e) {
                          e.printStackTrace();
                          onError(e);
                      }
                  }

                  @Override
                  public void onError(Throwable e) {
                      Timber.e(e);
                  }

                  @Override
                  public void onComplete() {

                  }
              });
    }

    /**
     * 合并首页banner及文章列表的实体
     */
    private static class ZipEntity {

        private WanAndroidResponse<List<BannerImg>> bannerResponse;

        private WanAndroidResponse<List<Article>> articleResponse;

        public ZipEntity(WanAndroidResponse<List<BannerImg>> bannerResponse,
                         WanAndroidResponse<List<Article>> articleResponse) {
            this.bannerResponse = bannerResponse;
            this.articleResponse = articleResponse;
        }

        public WanAndroidResponse<List<BannerImg>> getBannerResponse() {
            return bannerResponse;
        }

        public void setBannerResponse(WanAndroidResponse<List<BannerImg>> bannerResponse) {
            this.bannerResponse = bannerResponse;
        }

        public WanAndroidResponse<List<Article>> getArticleResponse() {
            return articleResponse;
        }

        public void setArticleResponse(WanAndroidResponse<List<Article>> articleResponse) {
            this.articleResponse = articleResponse;
        }
    }
}
