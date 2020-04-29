package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.NavContract;
import pers.jay.wanandroid.mvp.model.NavModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 10/18/2019 11:25
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class NavModule {

    @Binds
    abstract NavContract.Model bindNavModel(NavModel model);
}