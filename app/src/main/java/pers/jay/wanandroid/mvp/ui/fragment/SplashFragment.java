package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pers.jay.wanandroid.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.mvp.contract.SplashContract;
import pers.jay.wanandroid.mvp.presenter.SplashPresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SplashFragment extends BaseFragment<SplashPresenter> implements SplashContract.View {

    private MainActivity mainActivity;

    public static SplashFragment newInstance() {
         Bundle args = new Bundle();
         SplashFragment fragment = new SplashFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (mContext instanceof MainActivity) {
            mainActivity = ((MainActivity)mContext);
            mainActivity.setTheme(R.style.LaunchTheme);
        }
//        new Handler().postDelayed(() -> mainActivity.replaceFragment(ContainerFragment.newInstance()), 800);
        new Handler().postDelayed(() -> FragmentUtils.replace(this, ContainerFragment.newInstance(), false), 800);
    }

    @Override
    public void setData(@Nullable Object data) {

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
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
//            killMyself();
            ToastUtils.showShort("闪屏页被隐藏");
//            getFragmentManager().popBackStackImmediate();
        }
    }
}
