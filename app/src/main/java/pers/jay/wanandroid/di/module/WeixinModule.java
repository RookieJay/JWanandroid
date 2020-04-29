package pers.jay.wanandroid.di.module;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.WeixinContract;
import pers.jay.wanandroid.mvp.model.WeixinModel;

@Module
public abstract class WeixinModule {

    @Binds
    abstract WeixinContract.Model bindWeixinModel(WeixinModel model);

    @Singleton
    @Provides
    static WanAndroidService provideService() {
        return NetWorkManager.getInstance().getWanAndroidService();
    }
}