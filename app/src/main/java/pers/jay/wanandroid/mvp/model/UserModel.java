package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.ShareUserArticles;
import pers.jay.wanandroid.mvp.contract.UserContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class UserModel extends BaseModel implements UserContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public UserModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ShareUserArticles>> getUserArticles(long userId, int page) {
        return NetWorkManager.getInstance().getWanAndroidService().shareUserArticles(userId, page);
    }
}