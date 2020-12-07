package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pers.jay.wanandroid.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerCollectionComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.CollectionContract;
import pers.jay.wanandroid.mvp.presenter.CollectionPresenter;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class CollectionFragment extends BaseFragment<CollectionPresenter>
        implements CollectionContract.View {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int pageCount;
    private int page;
    private ArticleAdapter adapter;

    public static CollectionFragment newInstance() {
        return new CollectionFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerCollectionComponent //如找不到该类,请编译一下项目
                                  .builder()
                                  .appComponent(appComponent)
                                  .view(this)
                                  .build()
                                  .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appbar_recyclerview, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        assert mPresenter != null;
        setView();
        mPresenter.loadCollection(0);
    }

    private void setView() {
        initToolbar();
        initRecyclerView();
        fabTop.setOnClickListener(v -> scrollToTop());
    }

    private void initRecyclerView() {
        assert mPresenter != null;
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COLLECTION);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadCollection(page);
        }, mRecyclerView);
        adapter.setOnItemClickListener((adapter, view, position) -> switchToWebPage(position));
        adapter.setLikeListener(new ArticleAdapter.LikeListener() {
            @Override
            public void liked(Article item, int adapterPosition) {

            }

            @Override
            public void unLiked(Article item, int adapterPosition) {
                mPresenter.uncollectArticle(item, adapterPosition);
            }
        });
    }

    private void switchToWebPage(int position) {
        Intent intent = new Intent(mContext, X5WebActivity.class);
        Article article = adapter.getData().get(position);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, X5WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    private void initToolbar() {
        tvTitle.setText("我的收藏");
        toolbarLeft.setOnClickListener(v -> killMyself());
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
        FragmentUtils.pop(getFragmentManager(), true);
    }

    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public void showData(ArticleInfo data) {
        pageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        for (Article article : articles) {
            article.setCollect(true);
        }
        if (articles.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 0) {
            adapter.replaceData(articles);
        }
        else {
            adapter.addData(articles);
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void updateStatus(Article article, int position) {
        List<Article> articles = adapter.getData();
        Iterator<Article> it = articles.iterator();
        while (it.hasNext()) {
            Article data = it.next();
            if (data.getId() == article.getId()) {
                it.remove();
            }
        }
        adapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onBackPress() {
        killMyself();
        return true;
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

    private void loadAnimation(int type) {
        adapter.openLoadAnimation(type);
    }

    @Override
    public void onCollectSuccess(Article article, int position) {
    }

    @Override
    public void onCollectFail(Article article, int position) {

    }
}
