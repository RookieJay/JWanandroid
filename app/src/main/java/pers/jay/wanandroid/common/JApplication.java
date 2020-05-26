package pers.jay.wanandroid.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.jess.arms.base.App;
import com.jess.arms.base.BaseApplication;
import com.jess.arms.base.delegate.AppDelegate;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.Preconditions;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.Bugly;
import com.vondear.rxtool.RxTool;
import com.ycbjie.webviewlib.X5WebUtils;

import org.jetbrains.annotations.NotNull;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.utils.DarkModeUtils;
import pers.jay.wanandroid.utils.TimberUtils;
import pers.jay.wanandroid.widgets.PoemHeader;
import pers.zjc.commonlibs.util.CrashUtils;
import pers.zjc.commonlibs.util.SPUtils;
import pers.zjc.commonlibs.util.Utils;
import timber.log.Timber;

public class JApplication extends BaseApp implements App{

    //static 代码段可以防止内存泄露
    //此设置优先级是最低的，如果同时还使用了方法二、三，将会被其它方法取代
    static {
                //设置全局的Header构建器
                SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
                    @Override
                    public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                        layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                        //                        header.setPrimaryColors();
                        return new PoemHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
                    }
                });
                //设置全局的Footer构建器
                SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
                    @Override
                    public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                        //指定为经典Footer，默认是 BallPulseFooter
                        return new ClassicsFooter(context);
                    }
                });
    }

    private AppLifecycles mAppDelegate;
    private static JApplication mApplication;

    public static JApplication getInstance() {
        return mApplication;
    }

    /**
     * 这里会在 {@link BaseApplication#onCreate} 之前被调用,可以做一些较早的初始化
     * 常用于 MultiDex 以及插件化框架的初始化
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mAppDelegate == null) { this.mAppDelegate = new AppDelegate(base); }
        this.mAppDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mAppDelegate != null) {
            this.mAppDelegate.onCreate(this);
        }
        mApplication = this;
        // 暗黑模式
        loadDarkMode();
        // 设置log自动在apk为debug版本时打开，在release版本时关闭
        TimberUtils.setLogAuto();
        delayInit();
    }

    private void delayInit() {
        //设置线程的优先级，不与主线程抢资源
        HandlerThread thread = new HandlerThread("app_delay_init_thread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        //子线程初始化第三方组件.建议延迟初始化，可以发现是否影响其它功能，或者是崩溃！
        new Handler(thread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.init(JApplication.this);
                RxTool.init(getApp());
                // Bugly
                Bugly.init(getApplicationContext(), Const.APP_ID, false);
                // X5
                X5WebUtils.init(getApp());
                // 今日诗词
                JinrishiciFactory.init(getApp());
                // 开启 Debug 模式下可以打印网络请求日志
                RetrofitUrlManager.getInstance().setDebug(true);
                // 初始化sp
                SPUtils.create(getApplicationContext(), "cookies_prefs");
                CrashUtils.init();
            }
        }, 5000L);
    }



    public static void loadDarkMode() {
        int position = AppConfig.getInstance().getDarkModePosition();
        int mode = DarkModeUtils.getMode(position);
        Timber.e("当前模式 %s, mode:%s", DarkModeUtils.getName(position), mode);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAppDelegate != null) { this.mAppDelegate.onTerminate(this); }
    }

    /**
     * 将 {@link AppComponent} 返回出去, 供其它地方使用, {@link AppComponent} 接口中声明的方法所返回的实例, 在 {@link #getAppComponent()} 拿到对象后都可以直接使用
     *
     * @return AppComponent
     * @see ArmsUtils#obtainAppComponentFromContext(Context) 可直接获取 {@link AppComponent}
     */
    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppDelegate.class.getName());
        Preconditions.checkState(mAppDelegate instanceof App, "%s must be implements %s",
                mAppDelegate.getClass().getName(), App.class.getName());
        return ((App)mAppDelegate).getAppComponent();
    }

    /**
     * 重建Activity防止闪屏
     */
    public static void avoidSplashRecreate(Context context, Class clazz) {
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            context.startActivity(new Intent(context, clazz));
            ((Activity)context).overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_fade_in);
            ((Activity)context).finish();
        }

    }

    /**
     * 配置改变时回调
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        Timber.e("onConfigurationChanged：%s", "配置改变触发了");
        super.onConfigurationChanged(newConfig);
        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            // 夜间模式未启用，我们正在使用浅色主题
            case Configuration.UI_MODE_NIGHT_NO:
                Timber.e("onConfigurationChanged：%s", "夜间模式未启用");
                break;
            // 夜间模式启用，我们使用的是深色主题
            case Configuration.UI_MODE_NIGHT_YES:
                Timber.e("onConfigurationChanged：%s", "夜间模式已启用");
                break;
        }
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Timber.e("onConfigurationChanged：%s", "当前竖屏");
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            Timber.e("onConfigurationChanged：%s", "当前横屏");
        }
    }
}
