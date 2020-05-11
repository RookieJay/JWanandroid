package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.ShareUserArticles;
import pers.jay.wanandroid.mvp.contract.MySharesContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class MySharesModel extends BaseModel implements MySharesContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public MySharesModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ShareUserArticles>> getMyShares(int page) {
        return wanAndroidService.privateArticles(page);
    }

    @Override
    public Observable<WanAndroidResponse> deleteShare(int id) {
        return wanAndroidService.deleteArticle(id);
    }
}