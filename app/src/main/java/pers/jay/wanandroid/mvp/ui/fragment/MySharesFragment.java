package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.like.LikeButton;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerMySharesComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.MySharesContract;
import pers.jay.wanandroid.mvp.presenter.MySharesPresenter;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.SmartRefreshUtils;
import pers.jay.wanandroid.utils.UIUtils;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.zjc.commonlibs.util.FragmentUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class MySharesFragment extends BaseFragment<MySharesPresenter>
        implements MySharesContract.View {

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
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srlSquare)
    SmartRefreshLayout srlSquare;

    private ArticleAdapter mAdapter;

    private List<Article> mArticles = new ArrayList<>();
    private int page = 1;
    private int pageCount;

    public static MySharesFragment newInstance() {
        MySharesFragment fragment = new MySharesFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMySharesComponent //如找不到该类,请编译一下项目
                                .builder()
                                .appComponent(appComponent)
                                .view(this)
                                .build()
                                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_square, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initToolbar();
        initRefreshLayout();
        initRecyclerView();
        mPresenter.loadData(0);
    }

    private void initToolbar() {
        tvTitle.setText("我的分享");
        Glide.with(ivRight.getContext()).load(R.drawable.ic_add_24dp).into(ivRight);
        ivRight.setOnClickListener(v -> toSharePage());
        fabTop.setOnClickListener(v -> scrollTop());
        toolbarLeft.setOnClickListener(v -> killMyself());
    }

    private void scrollTop() {
        RvScrollTopUtils.smoothScrollTop(recyclerView);
    }

    private void toSharePage() {
        FragmentUtils.add(getChildFragmentManager(), ShareFragment.newInstance(), R.id.flContainer,
                true, R.anim.anim_fade_out, R.anim.anim_fade_in);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public boolean onBackPress() {
        killMyself();
        return true;
    }

    private void initRecyclerView() {
        mAdapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        ArmsUtils.configRecyclerView(recyclerView, new WrapContentLinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            if (pageCount == 1) {
                mAdapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadData(page);
        }, recyclerView);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(mContext, X5WebActivity.class);
            Article article = mAdapter.getData().get(position);
            intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
            intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
            launchActivity(intent);
        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            Article article = mAdapter.getData().get(position);
            UIUtils.createConfirmDialog(mContext, "您确认要删除这条分享吗？", true,
                    (dialog, which) -> mPresenter.delete(article.getId(), position), null).show();
            return true;
        });
        mAdapter.setLikeListener(new ArticleAdapter.LikeListener() {
            @Override
            public void liked(Article item, int position) {
                mPresenter.collectArticle(item, position);
            }

            @Override
            public void unLiked(Article item, int position) {
                mPresenter.collectArticle(item, position);
            }
        });
    }

    private void initRefreshLayout() {
        StoreHouseHeader header = new StoreHouseHeader(mContext);
        header.initWithString("WANANDROID");
        SmartRefreshUtils.with(srlSquare)
                         .pureScrollMode()
                         .setRefreshHeader(header)
                         .setRefreshListener(() -> {
                             if (mPresenter != null) {
                                 page = 1;
                                 mPresenter.loadData(page);
                             }
                         });
    }

    @Override
    public void showLoading() {
        srlSquare.autoRefreshAnimationOnly();
    }

    @Override
    public void hideLoading() {
        //        // 由于解绑时机发生在onComplete()之后，容易引起空指针
        //        if (srlSquare == null) {
        //            return;
        //        }
        srlSquare.finishRefresh();
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
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

    @Override
    public void showData(ArticleInfo data) {
        pageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        if (articles.isEmpty()) {
            mAdapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 1) {
            mArticles = data.getDatas();
            mAdapter.replaceData(mArticles);
            if (data.getPageCount() == data.getCurPage()) {
                mAdapter.loadMoreEnd();
            }
        }
        else {
            mArticles.addAll(data.getDatas());
            mAdapter.addData(data.getDatas());
            mAdapter.loadMoreComplete();
        }
    }

    @Override
    public void deleteSuccess(int position) {
        mAdapter.getData().remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Subscriber
    public void onShareSuccess(Event event) {
        if (event != null && event.getEventCode() == Const.EventCode.SHARE_SUCCESS) {
            mPresenter.loadData(0);
        }
    }

    @Override
    public void onCollectSuccess(Article article, int position) {

//        for (Article data : mAdapter.getData()) {
//            if (article.equals(data)) {
//                article.setCollect(!article.isCollect());
//            }
//        }
//        mAdapter.getData().remove(position);
//        article.setCollect(!article.isCollect());
//        mAdapter.getData().add(position, article);
//        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onCollectFail(Article article, int position) {
        mAdapter.restoreLike(position);
    }
}
