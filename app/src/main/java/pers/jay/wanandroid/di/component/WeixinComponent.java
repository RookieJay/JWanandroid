package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.WeixinModule;
import pers.jay.wanandroid.mvp.contract.WeixinContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.WeixinFragment;

@FragmentScope
@Component(modules = WeixinModule.class, dependencies = AppComponent.class)
public interface WeixinComponent {

    void inject(WeixinFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        WeixinComponent.Builder view(WeixinContract.View view);

        WeixinComponent.Builder appComponent(AppComponent appComponent);

        WeixinComponent build();
    }
}