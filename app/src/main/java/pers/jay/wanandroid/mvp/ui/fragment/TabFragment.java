package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerTabComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.mvp.contract.TabContract;
import pers.jay.wanandroid.mvp.presenter.TabPresenter;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

public class TabFragment extends BaseLazyLoadFragment<TabPresenter>
        implements TabContract.View, ScrollTopListener {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArticleAdapter adapter;
    private List<Article> mArticles = new ArrayList<>();
    private int page;
    private int pageCount;
    private int fromType = -1;
    private int cid = -1;

    public static TabFragment newInstance() {
        return new TabFragment();
    }

    public static Fragment create(Tab data, int position, int fromType) {
        TabFragment fragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.Key.KEY_TAB_FROM_TYPE, fromType);
        bundle.putInt(Const.Key.KEY_TAB_CHILD_POSITION, position);
        bundle.putParcelable(Const.Key.KEY_TAB_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerTabComponent.builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setView();
        initData();
    }

    private void initData() {
        Timber.e("initData");
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        fromType = bundle.getInt(Const.Key.KEY_TAB_FROM_TYPE);
        switch (fromType) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                initKnowledgeData(bundle);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
            case Const.Type.TYPE_TAB_PROJECT:
                initWeixinData(bundle);
                break;
            case -1:
            default:
                break;
        }
        if (mPresenter == null) {
            showMessage("presenter is null");
            return;
        }
        showLoading();
    }

    private void initKnowledgeData(Bundle bundle) {
        Tab child = bundle.getParcelable(Const.Key.KEY_TAB_DATA);
        int position = bundle.getInt(Const.Key.KEY_TAB_CHILD_POSITION, -1);
        if (child == null || position == -1) {
            showMessage("参数错误");
            return;
        }
        cid = child.getId();
    }

    private void initWeixinData(Bundle bundle) {
        Tab child = bundle.getParcelable(Const.Key.KEY_TAB_DATA);
        if (child == null) {
            showMessage("参数错误");
            return;
        }
        cid = child.getId();
    }

    private void refreshData() {
        mPresenter.requestArticles(cid, 0, fromType);
    }

    private void setView() {
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        ArmsUtils.configRecyclerView(mRecyclerView, new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        // 开启越界回弹
        refreshLayout.setEnableOverScrollBounce(true);
        refreshLayout.setEnableOverScrollDrag(true);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshData());
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            if (cid == -1) {
                return;
            }
            page++;
            mPresenter.requestArticles(cid, page, fromType);
        }, mRecyclerView);
        adapter.setOnItemClickListener((adapter, view, position) -> switchToWebPage(position));
        adapter.setLikeListener(new ArticleAdapter.LikeListener() {
            @Override
            public void liked(Article item, int adapterPosition) {
                mPresenter.collectArticle(item, adapterPosition);
            }

            @Override
            public void unLiked(Article item, int adapterPosition) {
                mPresenter.collectArticle(item, adapterPosition);
            }
        });
    }

    private void switchToWebPage(int position) {
        Article article = adapter.getData().get(position);
        Intent intent = new Intent(mContext, X5WebActivity.class);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, X5WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    @Override
    public void setData(@Nullable Object data) {
    }

    @Override
    public void showData(ArticleInfo data) {
        pageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        if (articles.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 0) {
            mArticles = data.getDatas();
            adapter.replaceData(mArticles);
        }
        else {
            mArticles.addAll(data.getDatas());
            adapter.addData(data.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
    }

    @Override
    protected void lazyLoadData() {
        Timber.e("lazyLoadData cid" + cid + " page" + page);
        refreshData();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        refreshLayout.finishRefresh();
    }

    @Override
    public void updateCollectStatus(boolean isCollect, Article item, int position) {
        for (Article article : adapter.getData()) {
            if (article.equals(item)) {
                article.setCollect(isCollect);
            }
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            if (isAdded() && isVisible() && getUserVisibleHint()) {
                lazyLoadData();
                mPresenter.requestArticles(cid, page, fromType);
            }
        }
    }

    /**
     * 退出登录
     */
    @Subscriber
    public void onLogout(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOG_OUT) {
            if (isAdded() && isVisible() && getUserVisibleHint()) {
                lazyLoadData();
                mPresenter.requestArticles(cid, page, fromType);
            }
        }
    }

    @Subscriber
    public void onAnimChanged(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.CHANGE_RV_ANIM) {
            Integer animType = (Integer)event.getData();
            if (animType != null && animType > 0) {
                loadAnimation(animType);
            }
        }
    }

    @Subscriber
    public void onArticleCollected(Event<Article> event) {
        if (null == event) {
            return;
        }
        if (event.getEventCode() == Const.EventCode.COLLECT_ARTICLE) {
            Article article = event.getData();
            for (Article item : adapter.getData()) {
                if (article.getId() == item.getId()) {
                    item.setCollect(article.isCollect());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void loadAnimation(int type) {
        adapter.openLoadAnimation(type);
    }
}
