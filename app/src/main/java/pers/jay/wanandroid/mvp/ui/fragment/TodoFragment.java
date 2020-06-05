package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerTodoComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.model.TodoSection;
import pers.jay.wanandroid.model.TodoType;
import pers.jay.wanandroid.mvp.contract.TodoContract;
import pers.jay.wanandroid.mvp.presenter.TodoPresenter;
import pers.jay.wanandroid.mvp.ui.activity.TodoEditActivity;
import pers.jay.wanandroid.mvp.ui.adapter.SimpleListAdapter;
import pers.jay.wanandroid.mvp.ui.adapter.TodoAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * @author ZJC
 */
public class TodoFragment extends BaseFragment<TodoPresenter>
        implements TodoContract.View, ScrollTopListener {

    public static final int TYPE_TODO = 1;
    public static final int TYPE_DONE = 2;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int mType;
    private int pageCount;
    private TodoAdapter adapter;
    private int page;

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_TODO_CAT = -1;
    private int mTodoCat = 1;

    private List<String> mOptions = new ArrayList<>();
    private RecyclerView rvOption;
    private SimpleListAdapter slAdapter;

    public void setTodoType(List<String> todoType) {
        this.todoType = todoType;
    }

    public List<String> getTodoType() {
        return todoType;
    }

    private List<String> todoType = new ArrayList<>();

    public static TodoFragment newInstance() {
        return new TodoFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerTodoComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setView();
        initArgs();
    }

    private void initArgs() {
        Bundle bundle = getArguments();
        if (null != bundle) {
            mType = bundle.getInt(Const.Key.KEY_FRAGMENT, 0);
            loadData(DEFAULT_PAGE, DEFAULT_TODO_CAT);
        }
    }

    private void setView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new TodoAdapter(R.layout.item_todo_content, R.layout.item_todo_title,
                new ArrayList<>());
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            loadData(page, mTodoCat);
        }, mRecyclerView);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            TodoSection data = this.adapter.getData().get(position);
            if (data.isHeader) {
                return;
            }
            switchToDetail(this.adapter.getData().get(position));
        });
        adapter.setOnItemLongClickListener((adapter, view, position) -> {
            TodoSection section = this.adapter.getData().get(position);
            if (section.isHeader) {
                return true;
            }
            showDialog(section.t, view, position);
            return true;
        });
    }

    private void showDialog(Todo todo, View view, int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view, Gravity.CENTER);
        popupMenu.inflate(R.menu.menu_todo_operation);
        if (mType == TYPE_DONE) {
            popupMenu.getMenu().findItem(R.id.todoDone).setVisible(false);
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.todoDone:
                    doneTodo(todo, position, popupMenu);
                    break;
                case R.id.todoDel:
                    deleteTodo(todo, position, popupMenu);
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void doneTodo(Todo todo, int position, PopupMenu popupMenu) {
        popupMenu.dismiss();
        mPresenter.doneTodo(todo, position);
    }

    private void deleteTodo(Todo todo, int position, PopupMenu popupMenu) {
        popupMenu.dismiss();
        mPresenter.deleteTodo(todo, position);
    }

    private void switchToDetail(TodoSection section) {
        Intent intent = new Intent(mContext, TodoEditActivity.class);
        intent.putExtra(Const.Key.KEY_TODO_TYPE, mType);
        intent.putExtra(Const.Key.KEY_TODO_CAT, mTodoCat);
        String title;
        switch (TodoType.parse(mTodoCat)) {
            case WORK:
                title = "工作";
                break;
            case LIFE:
                title = "生活";
                break;
            case ENTERTAINMAINT:
                title = "娱乐";
                break;
            default:
            case UNKNOWN:
                title = "";
        }
        intent.putExtra(Const.Key.KEY_TITLE, title);
        intent.putExtra(Const.Key.KEY_TODO, section.t);
        intent.putExtra(Const.Key.KEY_START_TYPE, TodoEditActivity.TYPE_EDIT);
        launchActivity(intent);
    }

    private void loadData(int page, int todoCat) {
        loadData(page, todoCat, false);
    }

    private void loadData(int page, int todoCat, boolean switchCat) {
        assert mPresenter != null;
        switch (mType) {
            case TYPE_TODO:
                mPresenter.loadTodoList(page, true, todoCat, switchCat);
                mOptions.add("完成");
                mOptions.add("删除");
                break;
            case TYPE_DONE:
                mPresenter.loadTodoList(page, false, todoCat, switchCat);
                mOptions.add("删除");
                break;
            default:
                break;
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        // 由于解绑时机发生在onComplete()之后，容易引起空指针
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        if (getFragmentManager() != null) {
            FragmentUtils.pop(getFragmentManager(), true);
        }
    }

    @Override
    public void showData(PageInfo data, List<TodoSection> sections, boolean switchType) {
        pageCount = data.getPageCount();
        if (sections.isEmpty()) {
            if (switchType) {
                adapter.setNewData(new ArrayList<>());
            }
            else {
                adapter.loadMoreEnd();
            }
            return;
        }
        if (data.getCurPage() == 0) {
            adapter.replaceData(sections);
        }
        else {
            adapter.addData(sections);
            adapter.loadMoreComplete();
        }
    }

    public void changeType(int todoCat) {
        mTodoCat = todoCat;
        loadData(page, todoCat, true);
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    public void refresh() {
        loadData(page, mTodoCat);
    }

    @Subscriber
    public void onCommitSuccess(Event event) {
        if (null != event) {
            if (event.getEventCode() == Const.EventCode.COMMIT_SUCCESS) {
                refresh();
            }
        }
    }

    @Override
    public void deleteSuccess(Todo todo, int position) {
//        List<TodoSection> list = adapter.getData();
//        for (int i = 0; i < list.size(); i++) {
//            TodoSection section = list.get(i);
//            if (section.isHeader) {
//                continue;
//            }
//            Todo data = section.t;
//            if (data.getId() == todo.getId()) {
//                adapter.remove(i);
//            }
//        }
//        adapter.notifyItemRemoved(position);
        refresh();
    }

    @Override
    public void updateSuccess(Todo todo, int position) {
        deleteSuccess(todo, position);
    }

}
