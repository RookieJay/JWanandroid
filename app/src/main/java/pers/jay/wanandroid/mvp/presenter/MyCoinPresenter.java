package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.CoinHistory;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;
import pers.jay.wanandroid.result.BaseWanBean;
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

    public void loadMyCoinHistory(int page) {
        mModel.getMyCoin(page)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse<PageInfo<CoinHistory>>>(mRootView) {

                  @Override
                  protected void onStart() {
                      mRootView.showLoading();
                  }

                  @Override
                  public void onSuccess(WanAndroidResponse<PageInfo<CoinHistory>> response) {
                      mRootView.showData(response.getData());
                  }
              });
    }

    /**
     * 获取排行榜第一名分数
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

    public void loadData() {
        Observable.zip(mModel.getMyCoin(1), mModel.getRank(1),
                new BiFunction<WanAndroidResponse<PageInfo<CoinHistory>>, WanAndroidResponse<PageInfo<Coin>>, CoinRank>() {
                    @Override
                    public CoinRank apply(WanAndroidResponse<PageInfo<CoinHistory>> hisResponse,
                                          WanAndroidResponse<PageInfo<Coin>> rankResponse) throws Exception {
                        PageInfo<CoinHistory> info = hisResponse.getData();
                        Coin coin = rankResponse.getData().getDatas().get(0);
                        return new CoinRank(info, coin);
                    }
                })
                  .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                  .compose(RxScheduler.Obs_io_main())
                  .subscribe(new Observer<CoinRank>() {
                      @Override
                      public void onSubscribe(Disposable d) {

                      }

                      @Override
                      public void onNext(CoinRank coinRank) {
                          mRootView.showData(coinRank.getInfo());
                          mRootView.showRank(coinRank.getCoin());
                      }

                      @Override
                      public void onError(Throwable e) {

                      }

                      @Override
                      public void onComplete() {

                      }
                  });
    }

    public void loadMyCoin() {
        mModel.personalCoin()
              .retryWhen(new RetryWithDelay(5, 3))
              .compose(RxLifecycleUtils.bindUntilEvent(mRootView, FragmentEvent.STOP))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<Coin>>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse<Coin> response) {
                      mRootView.setMyCoin(response.getData());
                  }
              });
    }

    class CoinRank extends BaseWanBean {

        private PageInfo<CoinHistory> info;

        private Coin coin;

        public CoinRank(PageInfo<CoinHistory> info, Coin coin) {
            this.info = info;
            this.coin = coin;
        }

        public PageInfo<CoinHistory> getInfo() {
            return info;
        }

        public void setInfo(PageInfo<CoinHistory> info) {
            this.info = info;
        }

        public Coin getCoin() {
            return coin;
        }

        public void setCoin(Coin coin) {
            this.coin = coin;
        }
    }
}
