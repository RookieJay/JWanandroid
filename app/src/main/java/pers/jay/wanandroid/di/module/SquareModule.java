package pers.jay.wanandroid.di.module;

import com.jess.arms.integration.IRepositoryManager;

import dagger.Binds;
import dagger.Module;

import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.SquareContract;
import pers.jay.wanandroid.mvp.model.SquareModel;

@Module
public abstract class SquareModule {

    @Binds
    abstract SquareContract.Model bindsquareModel(SquareModel model);

    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}