package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.SquareModule;
import pers.jay.wanandroid.mvp.contract.SquareContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.SquareFragment;

@FragmentScope
@Component(modules = SquareModule.class, dependencies = AppComponent.class)
public interface SquareComponent {

    void inject(SquareFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        SquareComponent.Builder view(SquareContract.View view);

        SquareComponent.Builder appComponent(AppComponent appComponent);

        SquareComponent build();
    }
}