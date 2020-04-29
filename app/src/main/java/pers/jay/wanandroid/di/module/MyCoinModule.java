package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;
import pers.jay.wanandroid.mvp.model.MyCoinModel;

@Module
public abstract class MyCoinModule {

    @Binds
    abstract MyCoinContract.Model bindMyCoinModel(MyCoinModel model);

    @Provides
    static WanAndroidService provideWanAndroidService() {
        return NetWorkManager.getInstance().getWanAndroidService();
    }
}