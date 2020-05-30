package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerTreeComponent;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.mvp.contract.TreeContract;
import pers.jay.wanandroid.mvp.presenter.KnowledgePresenter;
import pers.jay.wanandroid.mvp.ui.activity.TabActivity;
import pers.jay.wanandroid.mvp.ui.adapter.KnowledgeAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class KnowledgeFragment extends BaseLazyLoadFragment<KnowledgePresenter>
        implements TreeContract.View, KnowledgeAdapter.OnChildClickListener, ScrollTopListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private KnowledgeAdapter adapter;

    public static KnowledgeFragment newInstance() {
        return new KnowledgeFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerTreeComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
        mPresenter.requestTreeData();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    private void initView() {
        initRefreshLayout();
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new KnowledgeAdapter(R.layout.item_title, new ArrayList<>());
        adapter.setmOnChildClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
    }

    private void switchToTabActivity(int position, Tab data) {
        Intent intent = new Intent(mContext, TabActivity.class);
        intent.putExtra(Const.Key.KEY_TAB_FROM_TYPE, Const.Type.TYPE_TAB_KNOWLEDGE);
        intent.putExtra(Const.Key.KEY_TAB_DATA, data);
        intent.putExtra(Const.Key.KEY_TAB_CHILD_POSITION, position);
        launchActivity(intent);
    }

    private void initRefreshLayout() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> mPresenter.requestTreeData());
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
        if (refreshLayout == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        refreshLayout.finishRefresh();
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
    }

    @Override
    public void showTreeData(List<Tab> tabs) {
        adapter.replaceData(tabs);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void lazyLoadData() {
        showLoading();
        mPresenter.requestTreeData();
    }

    @Override
    public void onChildClick(int position, Tab data) {
        switchToTabActivity(position, data);
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(recyclerView);
    }

    @Override
    public void scrollToTopRefresh() {
        lazyLoadData();
    }

}
