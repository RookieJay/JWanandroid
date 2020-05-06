package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.http.RetryWithDelay;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.SearchResultContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class SearchResultPresenter
        extends BasePresenter<SearchResultContract.Model, SearchResultContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchResultPresenter(SearchResultContract.Model model,
                                 SearchResultContract.View rootView) {
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

    public void search(int page, String key, boolean refresh) {
        mModel.search(page, key)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  protected void onStart() {
                      if (refresh) {
                          mRootView.showLoading();
                      }
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo data = response.getData();
                      if (data != null) {
                          mRootView.showData(data, refresh);
                      }
                      else {
                          mRootView.showEmpty();
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
                  });
    }
}
