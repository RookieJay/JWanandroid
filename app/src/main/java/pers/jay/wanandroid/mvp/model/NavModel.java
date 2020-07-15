package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import java.nio.file.WatchService;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Navi;
import pers.jay.wanandroid.mvp.contract.NavContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class NavModel extends BaseModel implements NavContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public NavModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<List<Navi>>> getNavData() {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).naviData();
    }
}