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
import com.classic.common.MultipleStatusView;
import com.jess.arms.base.BaseLazyLoadFragment;
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
import pers.jay.wanandroid.common.CollectHelper;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerSquareComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.SquareContract;
import pers.jay.wanandroid.mvp.presenter.SquarePresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.SmartRefreshUtils;
import pers.zjc.commonlibs.util.FragmentUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static pers.jay.wanandroid.common.Const.Key.KEY_TITLE;

public class SquareFragment extends BaseLazyLoadFragment<SquarePresenter>
        implements SquareContract.View {

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
    @BindView(R.id.statusView)
    MultipleStatusView statusView;

    private ArticleAdapter mAdapter;

    private List<Article> mArticles = new ArrayList<>();
    private int page;
    private int pageCount;

    public static SquareFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        SquareFragment fragment = new SquareFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSquareComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
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
    }

    private void initToolbar() {
        if (getArguments() != null) {
            String title = getArguments().getString(KEY_TITLE);
            tvTitle.setText(title);
        }
        ivLeft.setVisibility(View.GONE);
        Glide.with(ivRight.getContext()).load(R.drawable.ic_add_24dp).into(ivRight);
        ivRight.setOnClickListener(v -> toSharePage());
        fabTop.setOnClickListener(v -> scrollTop());
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

    private void initRecyclerView() {
        mAdapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        ArmsUtils.configRecyclerView(recyclerView, new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 0 && pageCount == page + 1)) {
                mAdapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadData(page);
        }, recyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mContext, X5WebActivity.class);
                Article article = mAdapter.getData().get(position);
                intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
                intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
                launchActivity(intent);
            }
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
                                page = 0;
                                mPresenter.loadData(page);
                            }
                        });
    }

    @Override
    public void showLoading() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        srlSquare.finishRefresh();
        statusView.showContent();
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

    }

    @Override
    protected void lazyLoadData() {
        mPresenter.loadData(page);
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
        }
        else {
            mArticles.addAll(data.getDatas());
            mAdapter.addData(data.getDatas());
            mAdapter.loadMoreComplete();
        }
    }

    @Subscriber
    public void onShareSuccess(Event event) {
        if (event != null && event.getEventCode() == Const.EventCode.SHARE_SUCCESS) {
            mPresenter.loadData(0);
        }
    }

    @Override
    public void onCollectSuccess(Article article, int position) {
    }

    @Override
    public void onCollectFail(Article article, int position) {
        mAdapter.restoreLike(position);
    }

    @Override
    public boolean onBackPress() {
        MainActivity activity = (MainActivity)getActivity();
        if (activity == null) {
            return false;
        }
        activity.switchToContainer();
        return true;
    }

    @Override
    public void showError(String msg) {
        statusView.showError(msg);
    }

    @Override
    public void showNoNetwork() {
        statusView.showNoNetwork();
    }
}
