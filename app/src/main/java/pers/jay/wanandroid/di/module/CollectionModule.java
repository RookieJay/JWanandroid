package pers.jay.wanandroid.di.module;

import com.jess.arms.integration.IRepositoryManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.CollectionContract;
import pers.jay.wanandroid.mvp.model.CollectionModel;

@Module
public abstract class CollectionModule {

    @Binds
    abstract CollectionContract.Model bindCollectionModel(CollectionModel model);

    @Provides
    static WanAndroidService provideWanAndroidService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }

}