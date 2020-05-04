package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.http.RetryWithDelay;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.ObjectUtils;
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
                    Timber.d("zip当前线程%s", Thread.currentThread().getName());
                    List<BannerImg> bannerImgs = bannerResponse.getData();
                    // 添加必应每日一图
                    BannerImg img = new BannerImg();
                    img.setTitle("每日一图");
                    img.setUrl(Const.Url.DAILY_BING);
                    img.setImagePath(Const.Url.DAILY_BING);
                    bannerImgs.add(0, img);
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
                  .retryWhen(new RetryWithDelay())
                  .subscribe(new BaseWanObserver<WanAndroidResponse<ZipEntity>>(mRootView) {

                      @Override
                      public void onSuccess(WanAndroidResponse<ZipEntity> response) {
                          Timber.d("onSuccess当前线程%s", Thread.currentThread().getName());
                          ZipEntity zipEntity = response.getData();
                          List<BannerImg> bannerImgs = zipEntity.getBannerResponse().getData();
                          List<Article> articles = zipEntity.getArticleResponse().getData();
                          if (ObjectUtils.isNotEmpty(bannerImgs)) {
                              mRootView.showBanner(bannerImgs);
                          }
                          if (ObjectUtils.isNotEmpty(articles)) {
                              mRootView.refresh(articles);
                          }
                      }
                  });
    }
    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        Observable<WanAndroidResponse> observable;
        boolean isCollect = article.isCollect();
        if (!isCollect) {
            observable = mModel.collect(article.getId());
        }
        else {
            observable = mModel.unCollect(article.getId());
        }
        observable.compose(RxScheduler.Obs_io_main())
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .retryWhen(new RetryWithDelay(1000L))
                  .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                      @Override
                      public void onSuccess(WanAndroidResponse wanAndroidResponse) {
                          //                        mRootView.showMessage(!isCollect ? "收藏成功" : "取消收藏成功");
                          mRootView.updateCollectStatus(!isCollect, article, position);
                      }

                      @Override
                      public void onError(Throwable e) {
                          super.onError(e);
                          mRootView.restoreLikeButton(position);
                      }

                      @Override
                      protected void onException(ExceptionReason reason) {
                          super.onException(reason);
                          mRootView.restoreLikeButton(position);
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
