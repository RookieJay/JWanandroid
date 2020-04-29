package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.TodoEditModule;
import pers.jay.wanandroid.mvp.contract.TodoEditContract;

import com.jess.arms.di.scope.ActivityScope;

import pers.jay.wanandroid.mvp.ui.activity.TodoEditActivity;

@ActivityScope
@Component(modules = TodoEditModule.class, dependencies = AppComponent.class)
public interface TodoEditComponent {

    void inject(TodoEditActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TodoEditComponent.Builder view(TodoEditContract.View view);

        TodoEditComponent.Builder appComponent(AppComponent appComponent);

        TodoEditComponent build();
    }
}