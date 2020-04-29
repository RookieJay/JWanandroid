package pers.jay.wanandroid.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import com.scwang.smartrefresh.header.MaterialHeader;
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

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import pers.jay.wanandroid.BuildConfig;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.utils.DarkModeUtils;
import pers.jay.wanandroid.utils.TimberUtils;
import pers.zjc.commonlibs.util.SPUtils;
import timber.log.Timber;

public class JApplication extends Application implements App {

    //static 代码段可以防止内存泄露
    //此设置优先级是最低的，如果同时还使用了方法二、三，将会被其它方法取代
    static {
                //设置全局的Header构建器
                SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
                    @Override
                    public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                        layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                        return new MaterialHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
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
        //设置log自动在apk为debug版本时打开，在release版本时关闭
        TimberUtils.setLogAuto();
        RetrofitUrlManager.getInstance().setDebug(true);
        SPUtils.create(this, "cookies_prefs");
        if (BuildConfig.DEBUG) {
//            DoraemonKit.install(this);
//            DoraemonKit.hide();
        }
        RxTool.init(this);
        //Bugly
        Bugly.init(getApplicationContext(), Const.APP_ID, false);
        // X5
        X5WebUtils.init(this);
        // 暗黑模式
        loadDarkMode();
        JinrishiciFactory.init(this);
    }



    public static void loadDarkMode() {
        int mode = DarkModeUtils.getMode(AppConfig.getInstance().getDarkModePosition());
        Timber.e("mode:%s", mode);
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
     * @param activity
     */
    public static void avoidSplashRecreate(Activity activity, Class clazz) {
        if (activity == null) {
            return;
        }
        activity.startActivity(new Intent(activity, clazz));
        activity.overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_fade_in);
        activity.finish();
    }
}
