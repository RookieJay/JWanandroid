package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.X5Module;
import pers.jay.wanandroid.mvp.contract.X5Contract;

import com.jess.arms.di.scope.ActivityScope;

import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;

@ActivityScope
@Component(modules = X5Module.class, dependencies = AppComponent.class)
public interface X5Component {

    void inject(X5WebActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        X5Component.Builder view(X5Contract.View view);

        X5Component.Builder appComponent(AppComponent appComponent);

        X5Component build();
    }
}