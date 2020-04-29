package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.TodoModule;
import pers.jay.wanandroid.mvp.contract.TodoContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.TodoFragment;

@FragmentScope
@Component(modules = TodoModule.class, dependencies = AppComponent.class)
public interface TodoComponent {

    void inject(TodoFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TodoComponent.Builder view(TodoContract.View view);

        TodoComponent.Builder appComponent(AppComponent appComponent);

        TodoComponent build();
    }
}