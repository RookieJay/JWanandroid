package pers.jay.wanandroid.di.module;

import dagger.Binds;
import dagger.Module;
import pers.jay.wanandroid.mvp.contract.SearchContract;
import pers.jay.wanandroid.mvp.model.SearchModel;

@Module
public abstract class SearchModule {

    @Binds
    abstract SearchContract.Model bindSearchModel(SearchModel model);
}