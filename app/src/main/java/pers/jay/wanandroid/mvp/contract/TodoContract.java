package pers.jay.wanandroid.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.model.TodoSection;
import pers.jay.wanandroid.result.WanAndroidResponse;

/**
 * @author ZJC
 */
public interface TodoContract {

    interface View extends IView {

        void showData(PageInfo info, List<TodoSection> todoSections, boolean switchType);

        void setTodoType(List<String> todoType);

        void deleteSuccess(Todo todo, int position);

        void updateSuccess(Todo todo, int position);
    }

    interface Model extends IModel {

        Observable<WanAndroidResponse<PageInfo<Todo>>> getTodoList(int page, LinkedHashMap<String, Object> map);

        Observable<WanAndroidResponse> updateTodo(int id, LinkedHashMap<String, Object> map);

        Observable<WanAndroidResponse> deleteTodo(int id);
    }
}
