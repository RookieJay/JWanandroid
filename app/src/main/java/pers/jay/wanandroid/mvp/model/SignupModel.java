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
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.mvp.contract.SignupContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class SignupModel extends BaseModel implements SignupContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public SignupModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<User>> signUp(String username, String password,
                                                       String repassword) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).register(username, password, repassword);
    }

    @Override
    public Observable<WanAndroidResponse<User>> login(String userName, String password) {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService.class).login(userName, password);
    }
}