package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.SettingsModule;
import pers.jay.wanandroid.mvp.contract.SettingsContract;

import com.jess.arms.di.scope.ActivityScope;

import pers.jay.wanandroid.mvp.ui.activity.SettingsActivity;

@ActivityScope
@Component(modules = SettingsModule.class, dependencies = AppComponent.class)
public interface SettingsComponent {

    void inject(SettingsActivity activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        SettingsComponent.Builder view(SettingsContract.View view);

        SettingsComponent.Builder appComponent(AppComponent appComponent);

        SettingsComponent build();
    }
}