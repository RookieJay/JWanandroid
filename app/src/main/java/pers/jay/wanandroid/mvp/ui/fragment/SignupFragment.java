package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;

import java.util.Objects;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.CommonTextWatcher;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerSignupComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.mvp.contract.SignupContract;
import pers.jay.wanandroid.mvp.presenter.SignupPresenter;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SignupFragment extends BaseFragment<SignupPresenter> implements SignupContract.View {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etUserName)
    TextInputEditText etUserName;
    @BindView(R.id.tilUser)
    TextInputLayout tilUser;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    @BindView(R.id.tilPassword)
    TextInputLayout tilPassword;
    @BindView(R.id.etConfirm)
    TextInputEditText etConfirm;
    @BindView(R.id.tilConfirm)
    TextInputLayout tilConfirm;
    @BindView(R.id.btSignUp)
    AppCompatButton btSignUp;
    @BindView(R.id.llRoot)
    LinearLayout llRoot;

    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSignupComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        initToolbar();
        setListener();
    }

    private void initToolbar() {
        toolbarLeft.setOnClickListener(v -> killMyself());
        tvTitle.setText(getResources().getString(R.string.bt_signup));
    }

    private void setListener() {
        CommonTextWatcher watcher = new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String originPwd = Objects.requireNonNull(etPassword.getText(), "etPassword is null").toString().trim();
                String confirmPwd = s.toString().trim();
                if (originPwd.length() <= confirmPwd.length() && !StringUtils.equals(originPwd, confirmPwd)) {
                    tilConfirm.setError(getResources().getString(R.string.error_confirm_password));
                    tilConfirm.setErrorEnabled(true);
                } else {
                    tilConfirm.setError("");
                    tilConfirm.setErrorEnabled(false);
                }
            }
        };
        etConfirm.addTextChangedListener(watcher);
        btSignUp.setOnClickListener(v -> signUp());
    }

    private void enableClick(boolean enable) {
        btSignUp.setEnabled(enable);
        int enableColor = getResources().getColor(R.color.colorPrimary, null);
        int unableColor = getResources().getColor(R.color.gray, null);
        btSignUp.setBackgroundColor(enable ? enableColor : unableColor);
    }

    private void signUp() {
        String userName = Objects.requireNonNull(etUserName.getText(), "etUserName is null").toString().trim();
        String originPwd = Objects.requireNonNull(etPassword.getText(), "etPassword is null").toString().trim();
        String rePwd = Objects.requireNonNull(etConfirm.getText(), "etConfirm is null").toString().trim();
        mPresenter.signUp(userName, originPwd, rePwd);
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
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        Objects.requireNonNull(getFragmentManager()).popBackStackImmediate();
    }

    @Override
    public boolean onBackPress() {
        // 返回主页面
        if (getFragmentManager() != null) {
            killMyself();
        }
        return true;
    }

    @Override
    public void showSignUpSuccess(User user) {
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.SIGN_SUCCESS, user));
        killMyself();
    }
}
