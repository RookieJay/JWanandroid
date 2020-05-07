package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.ShareModule;
import pers.jay.wanandroid.mvp.contract.ShareContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.ShareFragment;

@FragmentScope
@Component(modules = ShareModule.class, dependencies = AppComponent.class)
public interface ShareComponent {

    void inject(ShareFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        ShareComponent.Builder view(ShareContract.View view);

        ShareComponent.Builder appComponent(AppComponent appComponent);

        ShareComponent build();
    }
}