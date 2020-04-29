package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import com.jess.arms.di.scope.FragmentScope;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.CoinHistory;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/16/2019 17:08
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
public class MyCoinModel extends BaseModel implements MyCoinContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public MyCoinModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<PageInfo<CoinHistory>>> getMyCoin(int page) {
        return wanAndroidService.coinHistory(page);
    }

    @Override
    public Observable<WanAndroidResponse<PageInfo<Coin>>> getRank(int page) {
        return wanAndroidService.allRank(page);
    }
}