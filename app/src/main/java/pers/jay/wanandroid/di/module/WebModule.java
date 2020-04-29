package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.WebContract;
import pers.jay.wanandroid.mvp.model.WebModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 08/19/2019 15:29
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class WebModule {

    @Binds
    abstract WebContract.Model bindWebModel(WebModel model);
}