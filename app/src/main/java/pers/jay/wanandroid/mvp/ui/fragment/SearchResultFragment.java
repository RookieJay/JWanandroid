package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerSearchResultComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.SearchResultContract;
import pers.jay.wanandroid.mvp.presenter.SearchResultPresenter;
import pers.jay.wanandroid.mvp.ui.activity.SearchActivity;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SearchResultFragment extends BaseFragment<SearchResultPresenter>
        implements SearchResultContract.View, SearchActivity.OnSearchListener {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    private String mSearchKey;
    private ArticleAdapter adapter;
    private int page;
    private int pageCount = -1;
    private SearchActivity searchActivity;

    public static SearchResultFragment newInstance() {
        return new SearchResultFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSearchResultComponent //如找不到该类,请编译一下项目
                                    .builder()
                                    .appComponent(appComponent)
                                    .view(this)
                                    .build()
                                    .inject(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.searchActivity = (SearchActivity)context;
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (searchActivity != null) {
            searchActivity.setOnSearchListener(this);
        }
        setupView();
    }

    private void setupView() {
        assert mPresenter != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> switchToWebPage(position));
        adapter.setOnLoadMoreListener(() -> {
            if (pageCount != 0 && pageCount == page + 1) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.search(page, mSearchKey, false);
        }, mRecyclerView);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Article article = (Article)adapter.getData().get(position);
            mPresenter.collectArticle(article, view, position);
        });
        fabTop.show();
        fabTop.setOnClickListener(v -> RvScrollTopUtils.smoothScrollTop(mRecyclerView));
    }

    private void switchToWebPage(int position) {
        Intent intent = new Intent(mContext, X5WebActivity.class);
        Article article = adapter.getData().get(position);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
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
        searchActivity.showSearchFragment();
    }

    @Override
    public void onSearch(String key) {
        searchActivity.showResultFragment();
        if (isAdded() && getUserVisibleHint()) {
            mSearchKey = key;
            assert mPresenter != null;
            mPresenter.search(0, key, true);
        }
    }

    @Override
    public void showEmpty() {
        showMessage("暂无数据");
    }

    @Override
    public void showData(ArticleInfo data, boolean refresh) {
        if (pageCount == -1) {
            this.pageCount = data.getPageCount();
        }
        if (data.getDatas().isEmpty()) {
            adapter.loadMoreEnd();
        } else {
            if (data.getCurPage() == 1) {
                adapter.replaceData(data.getDatas());
            } else {
                adapter.addData(data.getDatas());
                adapter.loadMoreComplete();
            }
        }
    }

    @Override
    public void updateCollectStatus(boolean collect, Article item, View view, int position) {
        for (Article article : adapter.getData()) {
            if (article.equals(item)) {
                article.setCollect(collect);
            }
        }
//        adapter.loadAnim((ImageView)view, collect);
        adapter.notifyItemChanged(position);
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
