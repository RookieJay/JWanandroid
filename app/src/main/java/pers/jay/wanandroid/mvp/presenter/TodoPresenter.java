package pers.jay.wanandroid.mvp.presenter;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.jess.arms.di.scope.FragmentScope;
import com.jess.arms.integration.AppManager;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.utils.RxLifecycleUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import pers.jay.wanandroid.base.BaseWanObserver;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.model.TodoSection;
import pers.jay.wanandroid.mvp.contract.TodoContract;
import pers.jay.wanandroid.result.WanAndroidResponse;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.TimeUtils;

/**
 * @author ZJC
 */
@FragmentScope
public class TodoPresenter extends BasePresenter<TodoContract.Model, TodoContract.View> {

    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;

    @Inject
    public TodoPresenter(TodoContract.Model model, TodoContract.View rootView) {
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

    public void loadTodoList(int page, boolean todo, int cat, boolean switchType) {
        LinkedHashMap<String, Object> map = getMapData(Todo.STATUS_TODO, cat, "",
                Todo.CREATE_DATE_DESC);
        if (!todo) {
            map = getMapData(Todo.STATUS_DONE, cat, "", Todo.DONE_DATE_DESC);
        }
        mModel.getTodoList(page, map)
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse<PageInfo<Todo>>>(mRootView) {
                  @Override
                  protected void onStart() {
                      if (page == 1) {
                          mRootView.showLoading();
                      }
                  }

                  @RequiresApi(api = Build.VERSION_CODES.N)
                  @Override
                  public void onSuccess(WanAndroidResponse<PageInfo<Todo>> response) {
                      PageInfo<Todo> info = response.getData();
                      List<Todo> todoList = info.getDatas();
                      List<TodoSection> todoSections = new ArrayList<>();
                      long date = 0L;
                      for (Todo todo : todoList) {
                          // 前后不为同一天，添加header
                          if (date == 0L || !isSameDay(date, todo.getDate())) {
                              TodoSection section = new TodoSection(true, todo.getDateStr());
                              todoSections.add(section);
                          }
                          todoSections.add(new TodoSection(todo));
                          date = todo.getDate();
                      }
                      //                      prepareTodoType(todoList);
                      mRootView.showData(info, todoSections, switchType);
                  }
              });
    }

    private LinkedHashMap<String, Object> getMapData(int status, int type, String priority,
                                                     int orderby) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        if (type != -1) {
            map.put("type", type);
        }
        if (!StringUtils.isEmpty(priority)) {
            map.put("priority", priority);
        }
        map.put("orderby", orderby);
        return map;
    }

    private void prepareTodoType(List<Todo> todoList) {
        if (todoList.isEmpty()) {
            return;
        }
        List<String> todoType = new ArrayList<>();
        for (Todo todo : todoList) {
            switch (todo.getTodoTypeEnum()) {
                case WORK:
                    todoType.add("工作");
                    break;
                case LIFE:
                    todoType.add("生活");
                    break;
                case ENTERTAINMAINT:
                    todoType.add("娱乐");
                    break;
                case UNKNOWN:
                default:
                    todoType.add("未知");
                    break;
            }
        }
        mRootView.setTodoType(todoType);
    }

    private boolean isSameDay(long date1, long date2) {
        return TimeUtils.getValueByCalendarField(date1,
                Calendar.DAY_OF_YEAR) == TimeUtils.getValueByCalendarField(date2,
                Calendar.DAY_OF_YEAR);
    }

    public void doneTodo(Todo todo, int position) {
        todo.setStatus(1);
        LinkedHashMap<String, Object> map = getDataMap(todo);
        updateTodo(todo, map, position);
    }

    public void deleteTodo(Todo todo, int position) {
        mModel.deleteTodo(todo.getId())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .compose(RxScheduler.Obs_io_main())
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      mRootView.showMessage("删除成功");
                      mRootView.deleteSuccess(todo, position);
                  }
              });
    }

    public void updateTodo(Todo todo, LinkedHashMap<String, Object> map, int position) {
        mModel.updateTodo(todo.getId(), map)
              .compose(RxScheduler.Obs_io_main())
              .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
              .subscribe(new BaseWanObserver<WanAndroidResponse>(mRootView) {
                  @Override
                  public void onSuccess(WanAndroidResponse response) {
                      mRootView.showMessage("已完成");
                      mRootView.updateSuccess(todo, position);
                      todo.setStatus(1);
                      EventBusManager.getInstance().post(new Event<>(Const.EventCode.TODO_DONE, todo));
                  }
              });
    }

    private LinkedHashMap<String, Object> getDataMap(Todo todo) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("title", todo.getTitle());
        map.put("type", todo.getType());
        map.put("priority", todo.getPriority());
        map.put("content", todo.getContent());
        map.put("date", todo.getDateStr());
        /* 0为未完成，1为完成 */
        map.put("status", 1);
        return map;
    }
}
