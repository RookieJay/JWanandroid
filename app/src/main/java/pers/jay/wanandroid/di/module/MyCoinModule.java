package pers.jay.wanandroid.di.module;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.integration.RepositoryManager;

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
    static WanAndroidService provideWanAndroidService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}