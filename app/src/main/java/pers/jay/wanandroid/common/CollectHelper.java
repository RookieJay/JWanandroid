package pers.jay.wanandroid.common;

import com.jess.arms.utils.RxLifecycleUtils;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

/**
 * 收藏/取消收藏辅助类
 */
public class CollectHelper {

    private static WanAndroidService wanService;
    private final ICollectView mRootView;
    private Article mArticle;
    private int mPosition;

    private CollectHelper(ICollectView view) {
        mRootView = view;
    }

    public static CollectHelper with(ICollectView view) {
        wanService = NetWorkManager.getInstance().getWanAndroidService();
        return new CollectHelper(view);
    }

    public CollectHelper target(Article article) {
        mArticle = article;
        return this;
    }

    public CollectHelper position(int position) {
        mPosition = position;
        return this;
    }

    public void collect() {
        if (wanService == null || mRootView == null) {
            throw new IllegalStateException("you must call 'with' method first");
        }
        if (mArticle == null) {
            throw new IllegalStateException("param 'article' in target method can not be null");
        }
        Observable<WanAndroidResponse> observable;
        boolean isCollect = mArticle.isCollect();
        if (!isCollect) {
            observable = wanService.collectInside(mArticle.getId());
        }
        else {
            observable = wanService.unCollect(mArticle.getId());
        }
        observable.compose(RxScheduler.Obs_io_main())
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                      @Override
                      public void onSuccess(WanAndroidResponse wanAndroidResponse) {
                          mRootView.onCollectSuccess(mArticle, mPosition);
                      }

                      @Override
                      public void onError(Throwable e) {
                          mRootView.onCollectFail(mArticle, mPosition);
                      }
                  });
    }

}
