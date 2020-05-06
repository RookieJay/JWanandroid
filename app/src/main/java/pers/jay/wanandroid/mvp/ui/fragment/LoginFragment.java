package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerLoginComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.mvp.contract.LoginContract;
import pers.jay.wanandroid.mvp.presenter.LoginPresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.BarUtils;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.KeyboardUtils;
import pers.zjc.commonlibs.util.ScreenUtils;
import pers.zjc.commonlibs.util.StringUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class LoginFragment extends BaseFragment<LoginPresenter> implements LoginContract.View {

    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.etUserName)
    TextInputEditText etUserName;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.btLogin)
    AppCompatButton btLogin;
    @BindView(R.id.tvToRegister)
    AppCompatTextView tvToRegister;
    @BindView(R.id.tilUser)
    TextInputLayout tilUser;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @Inject
    LoginPresenter loginPresenter;
    AppConfig appConfig;
    private int scrollToPosition;
    private boolean isScrolled;
    private View mRootView;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                             .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_login, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        /**
         * fragment点击穿透解决办法，从事件分发的角度来解决。
         */
        view.setClickable(true); //把View的click属性设为true，截断点击时间段扩散
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UIUtils.setSameColorBar(true, ((MainActivity)mContext).getWindow(), getResources());
        appConfig = AppConfig.getInstance();
        etUserName.setText(TextUtils.isEmpty(appConfig.getAccount()) ? "" : appConfig.getAccount());
        etPassword.setText(TextUtils.isEmpty(appConfig.getPassword()) ? "" : appConfig.getPassword());
        setListener();
    }



    @OnClick(R.id.btLogin)
    public void login(View view) {
        KeyboardUtils.hideSoftInput(view);
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        if (StringUtils.isEmpty(userName)) {
            tilUser.setError("请输入账号");
            tilUser.setErrorEnabled(true);
            return;
        } else {
            tilUser.setError("");
            tilUser.setErrorEnabled(false);
        }
        if (StringUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            tilPassword.setErrorEnabled(true);
            return;
        }
        else {
            tilPassword.setError("");
            tilPassword.setErrorEnabled(false);
        }
        loginPresenter.login(userName, password);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        configKeyboardEvent();
    }

    private void configKeyboardEvent() {
        KeyboardUtils.registerSoftInputChangedListener(getActivity(), keyboardHeight -> {
            // 顶层父布局
            scrollToPosition = 0;
            mRootView.postDelayed(() -> {
                int[] loc = new int[2];
                // 获取View左上顶点在屏幕中的绝对位置.(屏幕范围包括状态栏).
                tvToRegister.getLocationOnScreen(loc);
                int triggerToBtm = ScreenUtils.getScreenHeight() - loc[1] - tvToRegister.getHeight();
                if(triggerToBtm < keyboardHeight) {
                    // 输入框距离底部高度小于键盘高度，被遮挡，布局整体上移被遮挡的高度
                    scrollToPosition = keyboardHeight - triggerToBtm;
                    int navBarHeight = BarUtils.getNavBarHeight();
                    boolean navBarVisible = BarUtils.isNavBarVisible(getActivity().getWindow());
                    if(navBarVisible) {
                        // 底部导航栏显示时，加上导航栏高度
                        scrollToPosition += navBarHeight;
                    }
                    mRootView.scrollTo(0, scrollToPosition);
                    isScrolled = true;
                } else {
                    scrollToPosition = 0;
                }
            }, 10);
            // 软键盘消失，复原布局
            if (keyboardHeight == 0) {
                mRootView.scrollTo(0, 0);
            }
        });
    }

    private void setListener() {
        etUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (etUserName == null) {
                return;
            }
            if (!hasFocus && etUserName.getText()!= null && StringUtils.isEmpty(etUserName.getText().toString())) {
                tilUser.setError(getResources().getString(R.string.error_no_account));
                tilUser.setErrorEnabled(true);
            } else {
                tilUser.setError("");
                tilUser.setErrorEnabled(false);
            }
        });
        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (etPassword == null) {
                return;
            }
            if (!hasFocus && null != etPassword.getText() && StringUtils.isEmpty(etPassword.getText().toString())) {
                tilPassword.setError(getResources().getString(R.string.error_no_password));
                tilPassword.setErrorEnabled(true);
            } else {
                tilPassword.setError("");
                tilPassword.setErrorEnabled(false);
            }
        });
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPress() {
        // 返回主页面
        if (getFragmentManager() != null) {
            back();
        }
        return true;
    }

    @Override
    public void loginSuccess(User user) {
        if (getFragmentManager() != null) {
            back();
            EventBusManager.getInstance().post(new Event<>(Const.EventCode.LOGIN_SUCCESS, null));
        }
    }

    private void back() {
        FragmentUtils.pop(getFragmentManager(), true);
    }

}
