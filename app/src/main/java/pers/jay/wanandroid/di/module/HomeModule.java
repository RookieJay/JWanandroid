package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.mvp.model.HomeModel;


@Module
public abstract class HomeModule {

    @Binds
    abstract HomeContract.Model bindHomeModel(HomeModel model);

    @Provides
    static WanAndroidService provideService() {
        return NetWorkManager.getInstance().getWanAndroidService();
    }
}