package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.mvp.contract.SignupContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.StringUtils;
import timber.log.Timber;

@FragmentScope
public class SignupPresenter extends BasePresenter<SignupContract.Model, SignupContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SignupPresenter(SignupContract.Model model, SignupContract.View rootView) {
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

    public void signUp(String userName, String originPwd, String rePwd) {
        if (validate(userName, originPwd, rePwd)) {
            mModel.signUp(userName, originPwd, rePwd)
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .compose(RxScheduler.Obs_io_main())
                  .subscribe(new BaseWanObserver<WanAndroidResponse<User>>(mRootView) {
                      @Override
                      public void onSuccess(WanAndroidResponse<User> response) {
                          User user = response.getData();
                          if (user != null) {
                              user.setPassword(rePwd);
                              mRootView.showSignUpSuccess(user);
                          }
                      }
                  });
        }
    }

    private boolean validate(String userName, String originPwd, String rePwd) {
        if (StringUtils.isEmpty(userName)) {
            mRootView.showMessage(JApplication.getInstance().getString(R.string.error_no_account));
            return false;
        }
        if (StringUtils.isEmpty(originPwd)) {
            mRootView.showMessage(JApplication.getInstance().getString(R.string.error_no_password));
            return false;
        }
        if (StringUtils.isEmpty(rePwd)) {
            mRootView.showMessage(
                    JApplication.getInstance().getString(R.string.error_confirm_password));
            return false;
        }
        if (!StringUtils.equals(originPwd, rePwd)) {
            mRootView.showMessage(
                    JApplication.getInstance().getString(R.string.error_confirm_password));
            return false;
        }
        return true;
    }
}
