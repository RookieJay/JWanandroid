package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.QAModule;
import pers.jay.wanandroid.mvp.contract.QAContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.QAFragment;

@FragmentScope
@Component(modules = QAModule.class, dependencies = AppComponent.class)
public interface QAComponent {

    void inject(QAFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        QAComponent.Builder view(QAContract.View view);

        QAComponent.Builder appComponent(AppComponent appComponent);

        QAComponent build();
    }
}