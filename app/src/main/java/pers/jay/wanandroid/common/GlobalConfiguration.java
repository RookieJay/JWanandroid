package pers.jay.wanandroid.common;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.log.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;

import java.util.List;

import pers.jay.wanandroid.BuildConfig;

public class GlobalConfiguration implements ConfigModule {

    @Override
    public void applyOptions(@NonNull Context context,
                             @NonNull GlobalConfigModule.Builder builder) {
        if (!BuildConfig.LOG_DEBUG) { //Release 时, 让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
        }
//        builder.retrofitConfiguration(new ClientModule.RetrofitConfiguration() {
//            @Override
//            public void configRetrofit(@NonNull Context context,
//                                       @NonNull Retrofit.Builder builder) {
//                builder.baseUrl(Const.Url.WAN_ANDROID)
//                       .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                       .addConverterFactory(GsonConverterFactory.create());
//            }
//        }).okhttpConfiguration(new ClientModule.OkhttpConfiguration() {
//            @Override
//            public void configOkhttp(@NonNull Context context,
//                                     @NonNull OkHttpClient.Builder builder) {
//                builder.readTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
//                       .writeTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
//                       .callTimeout(Const.HttpConst.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
//            }
//        });
    }

    @Override
    public void injectAppLifecycle(@NonNull Context context,
                                   @NonNull List<AppLifecycles> lifecycles) {
        //AppLifecycles 中的所有方法都会在基类 Application 的对应生命周期中被调用, 所以在对应的方法中可以扩展一些自己需要的逻辑
        //可以根据不同的逻辑添加多个实现类
    }

    @Override
    public void injectActivityLifecycle(@NonNull Context context,
                                        @NonNull List<Application.ActivityLifecycleCallbacks> lifecycles) {
        //ActivityLifecycleCallbacks 中的所有方法都会在 Activity (包括三方库) 的对应生命周期中被调用, 所以在对应的方法中可以扩展一些自己需要的逻辑
        //可以根据不同的逻辑添加多个实现类
    }

    @Override
    public void injectFragmentLifecycle(@NonNull Context context,
                                        @NonNull List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        //FragmentLifecycleCallbacks 中的所有方法都会在 Fragment (包括三方库) 的对应生命周期中被调用, 所以在对应的方法中可以扩展一些自己需要的逻辑
        //可以根据不同的逻辑添加多个实现类
    }
}
