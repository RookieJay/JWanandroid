package pers.jay.wanandroid.mvp.model;

import android.app.Application;

import com.google.gson.Gson;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.reactivex.Observable;
import pers.jay.wanandroid.api.WanAndroidService;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.mvp.contract.TodoContract;
import pers.jay.wanandroid.result.WanAndroidResponse;

@FragmentScope
public class TodoModel extends BaseModel implements TodoContract.Model {

    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    @Inject
    WanAndroidService wanAndroidService;

    @Inject
    public TodoModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<WanAndroidResponse<PageInfo<Todo>>> getTodoList(int page, LinkedHashMap<String, Object> map) {
        return wanAndroidService.todoList(page, map);
    }

    @Override
    public Observable<WanAndroidResponse> updateTodo(int id, LinkedHashMap<String, Object> map) {
        return wanAndroidService.updateTodo(id, map);
    }

    @Override
    public Observable<WanAndroidResponse> deleteTodo(int id) {
        return wanAndroidService.deleteTodo(id);
    }
}