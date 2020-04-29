package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;

import pers.jay.wanandroid.mvp.contract.TreeContract;
import pers.jay.wanandroid.mvp.model.KnowledgeModel;

@Module
public abstract class TreeModule {

    @Binds
    abstract TreeContract.Model bindTreeModel(KnowledgeModel model);
}