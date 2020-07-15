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
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.QAContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class QAModel extends BaseModel implements QAContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public QAModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getQAList(int page) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).qaList(page);
    }
}