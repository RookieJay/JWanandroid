package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import pers.jay.wanandroid.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerNavComponent;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.Navi;
import pers.jay.wanandroid.mvp.contract.NavContract;
import pers.jay.wanandroid.mvp.presenter.NavPresenter;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.NaviAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.SmartRefreshUtils;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class NavFragment extends BaseLazyLoadFragment<NavPresenter>
        implements NavContract.View, ScrollTopListener, NaviAdapter.OnChildClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private NaviAdapter adapter;
    private List<Navi> mData = new ArrayList<>();

    public static NavFragment newInstance() {
        return new NavFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerNavComponent //如找不到该类,请编译一下项目
                           .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_knowledge, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        loadView();
    }

    private void loadView() {
        initRecyclerView();
        initRefreshLayout();
    }

    private void initRecyclerView() {
        adapter = new NaviAdapter(R.layout.item_title, new ArrayList<>());
        adapter.setmOnChildClickListener(this);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
    }

    private void initRefreshLayout() {
        SmartRefreshUtils.with(refreshLayout)
                         .pureScrollMode()
                         .setRefreshListener(() -> mPresenter.requestNavData());
    }

    private void switchToWebActivity(Article data) {
        Intent intent = new Intent(mContext, X5WebActivity.class);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, X5WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, data);
        launchActivity(intent);
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
        if (refreshLayout == null) {
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

    }

    @Override
    protected void lazyLoadData() {
        showLoading();
        mPresenter.requestNavData();
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(recyclerView);
    }

    @Override
    public void scrollToTopRefresh() {
        lazyLoadData();
    }

    @Override
    public void showNaviData(List<Navi> data) {
        this.mData = data;
        adapter.replaceData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, Navi data) {

    }

    @Override
    public void onItemChildClick(Article data) {
        switchToWebActivity(data);
    }
}
