package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.mvp.contract.ContainerContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class ContainerModel extends BaseModel implements ContainerContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public ContainerModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<Coin>> personalCoin() {
        return NetWorkManager.getInstance().getWanAndroidService().personalCoin();
    }

    @Override
    public Observable<WanAndroidResponse> logout() {
        return wanAndroidService.logout();
    }
}