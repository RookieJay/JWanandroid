package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.ResponseBody;
import pers.jay.wanandroid.api.ApiService;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.model.BingDailyImage;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class HomeModel extends BaseModel implements HomeContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;
    private ApiService apiService;

    @Inject
    public HomeModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = NetWorkManager.getInstance().getWanAndroidService();
        apiService = NetWorkManager.getInstance().getApiService(ApiService.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getArticle(int page) {
        return wanAndroidService.homeArticles(page);
    }

    @Override
    public Observable<WanAndroidResponse<List<BannerImg>>> getBanner() {
        return wanAndroidService.banner();
    }

    @Override
    public Observable<WanAndroidResponse<List<Article>>> getTopArticles() {
        return wanAndroidService.topArticles();
    }

    @Override
    public Observable<WanAndroidResponse> collect(int id) {
        return wanAndroidService.collectInside(id);
    }

    @Override
    public Observable<WanAndroidResponse> unCollect(int id) {
        return wanAndroidService.unCollect(id);
    }

    @Override
    public Observable<ResponseBody> getBingImg() {
        return apiService.bingImgUrl(Const.Url.DAILY_BING_GUOLIN);
    }

}