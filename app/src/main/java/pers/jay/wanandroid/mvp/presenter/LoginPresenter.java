package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.mvp.contract.LoginContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class LoginPresenter extends BasePresenter<LoginContract.Model, LoginContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public LoginPresenter(LoginContract.Model model, LoginContract.View rootView) {
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

    public void login(String userName, String password) {
        AppConfig.getInstance().setAccount(userName);
        AppConfig.getInstance().setPassword(password);
        mModel.login(userName, password)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new BaseWanObserver<WanAndroidResponse<User>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<User> result) {
                          if (result.getErrorCode() == Const.HttpConst.HTTP_CODE_SUCCESS) {
                              User user = result.getData();
                              if (user != null) {
                                  AppConfig.getInstance().setLogin(true);
                                  AppConfig.getInstance().setUserName(user.getUsername());
                                  mRootView.loginSuccess(user);
                              }
                          }
                          else {
                              mRootView.showMessage(result.getErrorMsg());
                          }
                  }

                  @Override
                  public void onError(Throwable e) {
                      super.onError(e);
                      AppConfig.getInstance().setAccount("");
                      AppConfig.getInstance().setPassword("");
                      AppConfig.getInstance().setLogin(false);
                  }
              });
    }
}
