package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.mvp.contract.ShareContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;

@FragmentScope
public class SharePresenter extends BasePresenter<ShareContract.Model, ShareContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public SharePresenter(ShareContract.Model model, ShareContract.View rootView) {
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

    public void share(String title, String link) {
        mModel.share(title, link)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse wanAndroidResponse) {
                      mRootView.success();
                  }
              });
    }

    public void parseTitleFromUrl(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                // 使用Jsoup做网页解析，需要在非UI线程进行
                Document document = Jsoup.parse(new URL(url), 5000);
                String title = document.head().getElementsByTag("title").text();
                emitter.onNext(title);
            }
        }).compose(RxScheduler.Obs_io_main()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String title) {
                mRootView.parseOut(title);
            }

            @Override
            public void onError(Throwable e) {
                mRootView.parseOut(Const.Url.BLANK);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
