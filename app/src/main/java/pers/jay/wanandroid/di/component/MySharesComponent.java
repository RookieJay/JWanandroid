package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.MySharesModule;
import pers.jay.wanandroid.mvp.contract.MySharesContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.MySharesFragment;

@FragmentScope
@Component(modules = MySharesModule.class, dependencies = AppComponent.class)
public interface MySharesComponent {

    void inject(MySharesFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        MySharesComponent.Builder view(MySharesContract.View view);

        MySharesComponent.Builder appComponent(AppComponent appComponent);

        MySharesComponent build();
    }
}