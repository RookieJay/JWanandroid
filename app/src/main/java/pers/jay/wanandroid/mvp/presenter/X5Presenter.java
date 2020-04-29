package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.http.RetryWithDelay;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.mvp.contract.X5Contract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@ActivityScope
public class X5Presenter extends BasePresenter<X5Contract.Model, X5Contract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;


    @Inject
    public X5Presenter(X5Contract.Model model, X5Contract.View rootView) {
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

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article) {
        if (null == article) {
            return;
        }
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
                          mRootView.updateCollectStatus(!isCollect, article);
                      }
                  });
    }
}
