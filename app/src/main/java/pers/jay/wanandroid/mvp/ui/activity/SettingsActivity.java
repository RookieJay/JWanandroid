package pers.jay.wanandroid.mvp.ui.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.tencent.bugly.beta.Beta;

import org.simple.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.di.component.DaggerSettingsComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.mvp.contract.SettingsContract;
import pers.jay.wanandroid.mvp.presenter.SettingsPresenter;
import pers.jay.wanandroid.mvp.ui.adapter.SimpleListAdapter;
import pers.jay.wanandroid.utils.DarkModeUtils;
import pers.jay.wanandroid.utils.RvAnimUtils;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.AppUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SettingsActivity extends BaseActivity<SettingsPresenter>
        implements SettingsContract.View, View.OnClickListener {

    @BindView(R.id.ll_dark_mode)
    LinearLayout llDarkMode;
    @BindView(R.id.tv_dark_mode)
    TextView tvDarkMode;
    @BindView(R.id.tv_rv_anim)
    TextView tvRvAnim;
    @BindView(R.id.ll_rv_anim)
    LinearLayout llRvAnim;
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
    @BindView(R.id.tv_check_update)
    TextView tvCheckUpdate;
    @BindView(R.id.ll_check_update)
    LinearLayout llCheckUpdate;
    private int mRvAnim;
    private int mDarkMode;
    private Layer animLayer;
    private Layer darkModeLayer;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerSettingsComponent //如找不到该类,请编译一下项目
                                .builder()
                                .appComponent(appComponent)
                                .view(this)
                                .build()
                                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_settings; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        UIUtils.setSameColorBar(true, getWindow(), getResources());
        mRvAnim = AppConfig.getInstance().getRvAnim();
        initView();
    }

    private void initView() {
        tvTitle.setText("系统设置");
        tvDarkMode.setText(DarkModeUtils.getName(AppConfig.getInstance().getDarkModePosition()));
        tvRvAnim.setText(RvAnimUtils.getName(AppConfig.getInstance().getRvAnim()));
        ivLeft.setOnClickListener(this);
        llRvAnim.setOnClickListener(this);
        llDarkMode.setOnClickListener(this);
        tvCheckUpdate.setText(AppUtils.getAppVersionName());
        llCheckUpdate.setOnClickListener(this);
    }

    private void showAnimPopWindow() {
        animLayer = AnyLayer.dialog(SettingsActivity.this)
                            .contentView(R.layout.layout_popup_list)
                            .cancelableOnTouchOutside(true)
                            .gravity(Gravity.BOTTOM)
                            .dragDismiss(DragLayout.DragStyle.Bottom)
                            .contentAnimator(new Layer.AnimatorCreator() {
                            @Override
                            public Animator createInAnimator(View target) {
                                return AnimatorHelper.createBottomInAnim(target);
                            }

                            @Override
                            public Animator createOutAnimator(View target) {
                                return AnimatorHelper.createBottomOutAnim(target);
                            }
                        })
                            .bindData(layer -> {
                            RecyclerView rv = layer.getView(R.id.mRecyclerView);
                            ArmsUtils.configRecyclerView(rv,
                                    new LinearLayoutManager(SettingsActivity.this));
                            List<String> list = Arrays.asList(
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.NONE),
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.ALPHAIN),
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.SCALEIN),
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_BOTTOM),
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_LEFT),
                                    RvAnimUtils.getName(RvAnimUtils.RvAnim.SLIDEIN_RIGHT));
                            SimpleListAdapter adapter = new SimpleListAdapter(list);
                            rv.setAdapter(adapter);
                            adapter.setOnItemClickListener((adapter1, view, position) -> {
                                tvRvAnim.setText(RvAnimUtils.getName(position));
                                AppConfig.getInstance().setRvAnim(position);
                                layer.dismiss();
                            });
                        });
        animLayer.show();
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
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivLeft:
                killMyself();
                break;
            case R.id.ll_dark_mode:
                setupDarkMode();
                break;
            case R.id.ll_rv_anim:
                showAnimPopWindow();
                break;
            case R.id.ll_check_update:
                Beta.checkUpgrade();
                break;
            default:
                break;
        }
    }

    private void setupDarkMode() {
        darkModeLayer = AnyLayer.dialog(SettingsActivity.this)
                            .contentView(R.layout.layout_popup_list)
                            .cancelableOnTouchOutside(true)
                            .gravity(Gravity.BOTTOM)
                            .dragDismiss(DragLayout.DragStyle.Bottom)
                            .contentAnimator(new Layer.AnimatorCreator() {
                                @Override
                                public Animator createInAnimator(View target) {
                                    return AnimatorHelper.createBottomInAnim(target);
                                }

                                @Override
                                public Animator createOutAnimator(View target) {
                                    return AnimatorHelper.createBottomOutAnim(target);
                                }
                            })
                            .bindData(layer -> {
                                RecyclerView rv = layer.getView(R.id.mRecyclerView);
                                ArmsUtils.configRecyclerView(rv,
                                        new LinearLayoutManager(SettingsActivity.this));
                                List<String> list = Arrays.asList(
                                        DarkModeUtils.getName(0),
                                        DarkModeUtils.getName(1),
                                        DarkModeUtils.getName(2),
                                        DarkModeUtils.getName(3));
                                SimpleListAdapter adapter = new SimpleListAdapter(list);
                                rv.setAdapter(adapter);
                                adapter.setOnItemClickListener((adapter1, view, position) -> {
                                    tvDarkMode.setText(DarkModeUtils.getName(position));
                                    AppConfig.getInstance().setDarkModePosition(position);
                                    JApplication.loadDarkMode();
                                    Timber.e("SettingsActivity准备重建");
                                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                                    overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_fade_in);
                                    finish();
//                                    JApplication.avoidSplashRecreate(SettingsActivity.this, SettingsActivity.class);
                                    EventBus.getDefault().post(new Event<>(Const.EventCode.CHANGE_UI_MODE, null));
                                    layer.dismiss();
                                });
                            });
        darkModeLayer.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        postSettingChangedEvent();
    }

    private void postSettingChangedEvent() {
        boolean rvAnimChanged = mRvAnim != AppConfig.getInstance().getRvAnim();
        if (rvAnimChanged) {
            Event<Integer> event = new Event<>(Const.EventCode.CHANGE_RV_ANIM,
                    AppConfig.getInstance().getRvAnim());
            EventBusManager.getInstance().post(event);
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        int currentNightMode = newConfig.uiMode & newConfig.UI_MODE_NIGHT_MASK;
//        switch (currentNightMode) {
//            // 夜间模式未启用，我们正在使用浅色主题
//            case Configuration.UI_MODE_NIGHT_NO:
//                Timber.e("onConfigurationChanged：%s", "夜间模式未启用");
//                break;
//            // 夜间模式启用，我们使用的是深色主题
//            case Configuration.UI_MODE_NIGHT_YES:
//                Timber.e("onConfigurationChanged：%s", "夜间模式启用");
//                break;
//        }
//        Timber.e("onConfigurationChanged：%s", "触发了");
//    }

}
