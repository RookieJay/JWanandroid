package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.LinkedHashMap;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.mvp.contract.TodoEditContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.StringUtils;

@ActivityScope
public class TodoEditPresenter
        extends BasePresenter<TodoEditContract.Model, TodoEditContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public TodoEditPresenter(TodoEditContract.Model model, TodoEditContract.View rootView) {
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

    public void addTodo(Todo mData) {
        if (checkEmpty(mData)) {
            return;
        }
        LinkedHashMap<String, Object> map = getDataMap(mData, false);
        mModel.addTodo(map)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      mRootView.commitSuccess();
                  }
              });
    }

    public void updateTodo(Todo mData) {
        if (checkEmpty(mData)) {
            return;
        }
        LinkedHashMap<String, Object> map = getDataMap(mData, true);
        mModel.updateTodo(mData.getId(), map)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      mRootView.commitSuccess();
                  }
              });
    }

    private boolean checkEmpty(Todo todo) {
        if (StringUtils.isEmpty(todo.getTitle())) {
            mRootView.showMessage("标题不能为空");
            return true;
        }
        if (todo.getType() == 0) {
            mRootView.showMessage("todo类型不能为空");
            return true;
        }
        if (todo.getPriority() == 0) {
            mRootView.showMessage("优先级不能为空");
            return true;
        }
        if (StringUtils.isEmpty(todo.getContent())) {
            mRootView.showMessage("内容不能为空");
            return true;
        }
        if (StringUtils.isEmpty(todo.getDateStr())) {
            mRootView.showMessage("日期不能为空");
            return true;
        }
        return false;
    }

    private LinkedHashMap<String, Object> getDataMap(Todo todo, boolean update) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("title", todo.getTitle());
        map.put("type", todo.getType());
        map.put("priority", todo.getPriority());
        map.put("content", todo.getContent());
        map.put("date", todo.getDateStr());
        if (update) {
            /* 0为未完成，1为完成 */
            map.put("status", 0);
        }
        return map;
    }

}
