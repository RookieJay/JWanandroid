package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.CookiesManager;
import pers.jay.wanandroid.di.module.ContainerModule;
import pers.jay.wanandroid.mvp.contract.ContainerContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.ContainerFragment;

@FragmentScope
@Component(modules = ContainerModule.class, dependencies = AppComponent.class)
public interface ContainerComponent {

    void inject(ContainerFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        ContainerComponent.Builder view(ContainerContract.View view);

        ContainerComponent.Builder appComponent(AppComponent appComponent);

        ContainerComponent build();
    }

    AppConfig appConfig();

    CookiesManager cookiesManager();
}