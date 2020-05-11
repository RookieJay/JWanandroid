package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;
import android.view.View;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.CollectHelper;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.http.RetryWithDelay;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.TabContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class TabPresenter extends BasePresenter<TabContract.Model, TabContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public TabPresenter(TabContract.Model model, TabContract.View rootView) {
        super(model, rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

    public void requestArticles(int childId, int page, int fromType) {
        switch (fromType) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                requestKnowledgeArticles(childId, page);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
                requestWeixinArticles(childId, page);
                break;
            case Const.Type.TYPE_TAB_PROJECT:
                requestProjectArticles(childId, page);
                break;
            default:
                mRootView.showMessage("未知类型");
                break;
        }
    }

    public void requestKnowledgeArticles(int childId, int page) {
        mModel.getKnowledgeArticles(childId, page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

              });
    }


    private void requestProjectArticles(int childId, int page) {
        mModel.getProjectArticles(page, childId)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

              });
    }

    /**
     * 收藏或取消收藏文章
     */
    public void collectArticle(Article article, int position) {
        CollectHelper.with(mRootView).target(article).position(position).collect();
    }

    public void requestWeixinArticles(int cid, int page) {
        mModel.getWxArticles(cid, page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .retryWhen(new RetryWithDelay(3000L))
              .subscribe(new BaseWanObserver<WanAndroidResponse<ArticleInfo>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<ArticleInfo> response) {
                      ArticleInfo articleInfo = response.getData();
                      mRootView.showData(articleInfo);
                  }

              });
    }
}
