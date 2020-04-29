package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.CollectionModule;
import pers.jay.wanandroid.mvp.contract.CollectionContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.CollectionFragment;

@FragmentScope
@Component(modules = CollectionModule.class, dependencies = AppComponent.class)
public interface CollectionComponent {

    void inject(CollectionFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        CollectionComponent.Builder view(CollectionContract.View view);

        CollectionComponent.Builder appComponent(AppComponent appComponent);

        CollectionComponent build();
    }
}