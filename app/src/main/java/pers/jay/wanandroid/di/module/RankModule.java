package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.RankContract;
import pers.jay.wanandroid.mvp.model.RankModel;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/13/2019 17:02
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class RankModule {

    @Binds
    abstract RankContract.Model bindRankModel(RankModel model);
}