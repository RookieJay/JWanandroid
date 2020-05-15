package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.SignupModule;
import pers.jay.wanandroid.mvp.contract.SignupContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.SignupFragment;

@FragmentScope
@Component(modules = SignupModule.class, dependencies = AppComponent.class)
public interface SignupComponent {

    void inject(SignupFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        SignupComponent.Builder view(SignupContract.View view);

        SignupComponent.Builder appComponent(AppComponent appComponent);

        SignupComponent build();
    }
}