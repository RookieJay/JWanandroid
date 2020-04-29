package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.LoginContract;
import pers.jay.wanandroid.mvp.model.LoginModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/12/2019 15:38
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class LoginModule {

    @Binds
    abstract LoginContract.Model bindLoginModel(LoginModel model);
}