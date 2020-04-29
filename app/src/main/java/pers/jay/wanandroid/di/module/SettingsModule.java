package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.SettingsContract;
import pers.jay.wanandroid.mvp.model.SettingsModel;

@Module
public abstract class SettingsModule {

    @Binds
    abstract SettingsContract.Model bindSettingsModel(SettingsModel model);
}