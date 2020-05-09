package pers.jay.wanandroid.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import pers.jay.wanandroid.mvp.contract.QAContract;
import pers.jay.wanandroid.mvp.model.QAModel;

@Module
public abstract class QAModule {

    @Binds
    abstract QAContract.Model bindQAModel(QAModel model);
}