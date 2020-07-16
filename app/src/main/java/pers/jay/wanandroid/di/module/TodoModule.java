package pers.jay.wanandroid.di.module;


import com.jess.arms.integration.IRepositoryManager;

import dagger.Binds;
import dagger.Module;

import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.TodoContract;
import pers.jay.wanandroid.mvp.model.TodoModel;

@Module
public abstract class TodoModule {

    @Binds
    abstract TodoContract.Model bindTodoModel(TodoModel model);

    @Provides
    static WanAndroidService provideService(IRepositoryManager repositoryManager) {
        return NetWorkManager.getInstance().getWanAndroidService(repositoryManager);
    }
}