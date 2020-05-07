package pers.jay.wanandroid.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import pers.jay.wanandroid.mvp.contract.MySharesContract;
import pers.jay.wanandroid.mvp.model.MySharesModel;

@Module
public abstract class MySharesModule {

    @Binds
    abstract MySharesContract.Model bindMySharesModel(MySharesModel model);
}