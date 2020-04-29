package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.mvp.contract.TodoEditContract;
import pers.jay.wanandroid.mvp.model.TodoEditModel;

@Module
public abstract class TodoEditModule {

    @Binds
    abstract TodoEditContract.Model bindTodoEditModel(TodoEditModel model);

    @Provides
    static WanAndroidService provideService() {
        return NetWorkManager.getInstance().getWanAndroidService();
    }
}