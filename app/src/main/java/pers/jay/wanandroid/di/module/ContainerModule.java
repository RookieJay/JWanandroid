package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.CookiesManager;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.ContainerContract;
import pers.jay.wanandroid.mvp.model.ContainerModel;

@Module
public abstract class ContainerModule {

    @Binds
    abstract ContainerContract.Model bindContainerModel(ContainerModel model);

    @Provides
    static AppConfig provideAppConfig() {
        return AppConfig.getInstance();
    }

    @Provides
    static CookiesManager provideCookiesManager() {
        return CookiesManager.getInstance();
    }

    @Provides
    static WanAndroidService provideWanAndroidService() {
        return NetWorkManager.getInstance().getWanAndroidService();
    }
}