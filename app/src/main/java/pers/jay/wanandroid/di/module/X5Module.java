package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import pers.jay.wanandroid.mvp.contract.X5Contract;
import pers.jay.wanandroid.mvp.model.X5Model;

@Module
public abstract class X5Module {

    @Binds
    abstract X5Contract.Model bindX5Model(X5Model model);
}