package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.CoinHistory;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.ObjectUtils;

@FragmentScope
public class MyCoinPresenter extends BasePresenter<MyCoinContract.Model, MyCoinContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public MyCoinPresenter(MyCoinContract.Model model, MyCoinContract.View rootView) {
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

    public void loadMyCoin(int page) {
        mModel.getMyCoin(page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<PageInfo<CoinHistory>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(
                          WanAndroidResponse<PageInfo<CoinHistory>> response) {
                      mRootView.showData(response.getData());
                  }
              });
    }

    /**
     * 获取排行榜第一名分数
     * @param page
     */
    public void loadRank(int page) {
        mModel.getRank(1)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<PageInfo<Coin>>>(mRootView) {

                  @Override
                  public void onSuccess(WanAndroidResponse<PageInfo<Coin>> response) {
                      PageInfo<Coin> info = response.getData();
                      List<Coin> coinList = info.getDatas();
                      if (ObjectUtils.isEmpty(coinList)) {
                          return;
                      }
                      Coin coin = coinList.get(0);
                      mRootView.showRank(coin);
                  }
              });
    }
}
