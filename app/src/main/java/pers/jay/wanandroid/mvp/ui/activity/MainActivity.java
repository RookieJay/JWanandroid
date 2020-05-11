package pers.jay.wanandroid.mvp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.di.component.DaggerMainComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.mvp.contract.MainContract;
import pers.jay.wanandroid.mvp.presenter.MainPresenter;
import pers.jay.wanandroid.mvp.ui.fragment.ContainerFragment;
import pers.jay.wanandroid.mvp.ui.fragment.LoginFragment;
import pers.jay.wanandroid.mvp.ui.fragment.SplashFragment;
import pers.jay.wanandroid.mvp.ui.fragment.SquareFragment;
import pers.zjc.commonlibs.ui.BasePagerAdapter;
import pers.zjc.commonlibs.util.ActivityUtils;
import pers.zjc.commonlibs.util.FragmentUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.flContainer)
    FrameLayout flContainer;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /* 当前fragment */
    private Fragment curFragment;

    private FragmentManager mFragmentManager;

    private BasePagerAdapter<String, Fragment> fragmentPagerAdapter;
    private boolean loginShowing;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initViewPager();
        // 灰度化方案二(清明节灰化app)，来源：鸿洋公众号：https://mp.weixin.qq.com/s/EioJ8ogsCxQEFm44mKFiOQ
        //        Paint paint = new Paint();
        //        ColorMatrix cm = new ColorMatrix();
        //        cm.setSaturation(0);
        //        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        //        getWindow().getDecorView().setLayerType(View.LAYER_TYPE_HARDWARE, paint);
    }

    private void initViewPager() {
        String[] titles = { "广场", "" };
        fragmentPagerAdapter = new BasePagerAdapter<>(getSupportFragmentManager(),
                new BasePagerAdapter.PagerFragCreator<String, Fragment>() {
                    @Override
                    public Fragment createFragment(String data, int position) {
                        switch (position) {
                            case 0:
                                return SquareFragment.newInstance(data);
                            case 1:
                            default:
                                return ContainerFragment.newInstance();
                        }
                    }

                    @Override
                    public String createTitle(String data) {
                        return data;
                    }
                });
        fragmentPagerAdapter.setData(Arrays.asList(titles));
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setCurrentItem(1);
    }

    private void setFullScreen(boolean isFullScreen) {
        setTheme(isFullScreen ? R.style.LaunchTheme : R.style.AppTheme);
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
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
    }

    /**
     * 统一切换fragment的方法,不实例化多个Fragment，避免卡顿,Fragment中执行状态切换后的回调onHiddenChanged(boolean hidden)
     *
     * @param targetFragment 跳转目标fragment
     */
    public void switchFragment(Fragment targetFragment, Bundle args) {
        if (targetFragment == null) {
            Timber.d("参数为空");
            return;
        }
        if (mFragmentManager != null) {
            FragmentTransaction trans = mFragmentManager.beginTransaction();
            // 转场自定义动画
            trans.setCustomAnimations(R.anim.translate_right_to_center,
                    R.anim.translate_center_to_left, R.anim.translate_left_to_center,
                    R.anim.translate_center_to_right);
            if (!targetFragment.isAdded()) {
                // 首次执行curFragment为空，需要判断
                if (curFragment != null) {
                    trans.hide(curFragment);
                }
                trans.add(R.id.flContainer, targetFragment,
                        targetFragment.getClass().getSimpleName());
                trans.addToBackStack(targetFragment.getClass().getSimpleName());
                //                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);    //设置动画效果

            }
            else {
                //                targetFragment = mFragmentManager.findFragmentByTag(targetFragment.getClass().getSimpleName());
                trans.hide(curFragment).show(targetFragment);
            }
            trans.commitAllowingStateLoss();
            if (curFragment instanceof SplashFragment) {
                trans.remove(curFragment);
            }
            curFragment = targetFragment;
        }
    }

    public void switchFragment(Fragment targetFragment) {
        switchFragment(targetFragment, null);
    }

    public void addFrag(Fragment targetFragment) {
        FragmentUtils.add(getSupportFragmentManager(), targetFragment, R.id.flContainer);
    }

    /**
     *
     */
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            FragmentTransaction trans = fm.beginTransaction();
            trans.replace(R.id.flContainer, fragment);
            trans.commitAllowingStateLoss();
        }
    }

    public void showHideFragment(Fragment sourceFragment, Fragment destFragment) {
        FragmentUtils.showHide(destFragment, sourceFragment);
    }

    public void switchToLogin() {
        FragmentUtils.add(mFragmentManager, LoginFragment.newInstance(), R.id.flContainer);
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    //    @Override
    //    public void onConfigurationChanged(Configuration newConfig) {
    //        super.onConfigurationChanged(newConfig);
    //        // 检测到暗黑模式启动或关闭，则重建
    //        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
    //        int position = AppConfig.getInstance().getDarkModePosition();
    //        int mode = DarkModeUtils.getMode(position);
    //        switch (currentNightMode) {
    //            // 夜间模式未启用，我们正在使用浅色主题
    //            case Configuration.UI_MODE_NIGHT_NO:
    //                if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
    //                    showMessage("启动白天模式");
    //                    JApplication.avoidSplashRecreate(this, MainActivity.class);
    //                }
    //                break;
    //            // 夜间模式启用，我们使用的是深色主题
    //            case Configuration.UI_MODE_NIGHT_YES:
    //                if (mode == AppCompatDelegate.MODE_NIGHT_NO) {
    //                    showMessage("启动黑暗模式");
    //                    JApplication.avoidSplashRecreate(this, MainActivity.class);
    //                }
    //                break;
    //            default:
    //                break;
    //        }
    //
    //    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Activity 意外销毁时保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }

    @Subscriber
    public void onUiModeChanged(Event event) {
        if (event.getEventCode() == Const.EventCode.CHANGE_UI_MODE) {
            Timber.e("onUiModeChanged-MainActivity准备重建");
            JApplication.avoidSplashRecreate(this, MainActivity.class);
        }
    }

    @Subscriber
    public void onTokenExpiredEvent(Event event) {
        if (null != event) {
            if (event.getEventCode() == Const.EventCode.LOGIN_EXPIRED && this.equals(
                    ActivityUtils.getTopActivity()) && !loginShowing) {
                showMessage(getString(R.string.error_login_expired));
                switchFragment(LoginFragment.newInstance());
                loginShowing = true;
            }
        }
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && (event.getEventCode() == Const.EventCode.LOGIN_SUCCESS || event.getEventCode() == Const.EventCode.LOGIN_RETURN)) {
            loginShowing = false;
        }
    }

    public void switchToContainer() {
        viewPager.setCurrentItem(1);
    }
}
