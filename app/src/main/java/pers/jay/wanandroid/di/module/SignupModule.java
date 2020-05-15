package pers.jay.wanandroid.di.module;

import com.jess.arms.di.scope.FragmentScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import pers.jay.wanandroid.mvp.contract.SignupContract;
import pers.jay.wanandroid.mvp.model.SignupModel;

@Module
public abstract class SignupModule {

    @Binds
    abstract SignupContract.Model bindSignupModel(SignupModel model);
}