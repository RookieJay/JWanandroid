package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.model.HotKey;
import pers.jay.wanandroid.mvp.contract.SearchContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@ActivityScope
public class SearchPresenter extends BasePresenter<SearchContract.Model, SearchContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SearchPresenter(SearchContract.Model model, SearchContract.View rootView) {
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

    public void loadHotKeys() {
        mModel.getHotKeys()
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<List<HotKey>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<List<HotKey>> response) {
                      List<HotKey> data = response.getData();
                      if (data != null && !data.isEmpty()) {
                          mRootView.showHotKeys(data);
                      } else {
                          mRootView.showEmpty();
                      }

                  }
              });

    }
}
