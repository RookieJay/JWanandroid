package pers.jay.wanandroid.common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class BaseApp extends Application implements Application.ActivityLifecycleCallbacks {

    private static BaseApp application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityLifecycleCallbacks(this);
    }

    public static Application getApp() {
        if (application == null) {
            throw new NullPointerException("App is not registered in the manifest");
        } else {
            return application;
        }
    }

    /**
     * 判断Android程序是否在前台运行
     */
    public static boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getAppContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        String packageName = getAppContext().getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    private static Context getAppContext() {
        return application.getApplicationContext();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
