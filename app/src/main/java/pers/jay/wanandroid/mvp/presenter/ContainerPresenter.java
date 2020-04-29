package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.CookiesManager;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.mvp.contract.ContainerContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class ContainerPresenter
        extends BasePresenter<ContainerContract.Model, ContainerContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    @Inject
    AppConfig appConfig;
    @Inject
    CookiesManager cookiesManager;

    @Inject
    public ContainerPresenter(ContainerContract.Model model, ContainerContract.View rootView) {
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

    public void loadCoin() {
        mModel.personalCoin()
              .retryWhen(new RetryWithDelay(5, 3))
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<Coin>>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse<Coin> response) {
                      mRootView.showCoin(response.getData());
                  }
              });
    }

    public void logout() {
        mModel.logout()
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      appConfig.setLogin(false);
                      cookiesManager.clearCookies();
                      mRootView.showLogoutSuccess();
                  }
              });
    }

    public void loadPoem() {
        JinrishiciClient client = new JinrishiciClient();
        client.getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                if (poetySentence != null && poetySentence.getData() != null) {
                    String content = poetySentence.getData().getContent();
                    mRootView.showPoem(content);
                }
            }

            @Override
            public void error(JinrishiciRuntimeException e) {

            }
        });
    }
}
