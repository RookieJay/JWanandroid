package pers.jay.wanandroid.mvp.model;

import com.google.gson.Gson;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;
import pers.jay.wanandroid.api.ApiService;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.model.dao.ArticleDao;
import pers.jay.wanandroid.model.dao.ArticleInfoDao;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class HomeModel extends BaseModel implements HomeContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    private WanAndroidService wanAndroidService;
    private ApiService apiService;
    private ArticleDao articleDao;
    private ArticleInfoDao articleInfoDao;

    @Inject
    public HomeModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
        wanAndroidService = NetWorkManager.getInstance().getWanAndroidService();
        apiService = NetWorkManager.getInstance().getApiService(ApiService.class);
        articleDao = JApplication.getInstance().getDaoSession().getArticleDao();
        articleInfoDao = JApplication.getInstance().getDaoSession().getArticleInfoDao();
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

    @Override
    public Observable<WanAndroidResponse<ArticleInfo>> getArticleLocal() {
        List<Article> tops = articleDao.queryBuilder()
                                       .where(ArticleDao.Properties.IsTop.eq(true))
                                       .list();
        List<Article> articles = articleDao.queryBuilder()
                                           .where(ArticleDao.Properties.IsTop.eq(false))
                                           .list();
        articles.addAll(tops);
        return null;
    }

}