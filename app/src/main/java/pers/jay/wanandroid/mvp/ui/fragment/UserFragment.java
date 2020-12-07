package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.classic.common.MultipleStatusView;

import pers.jay.wanandroid.base.BaseFragment;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerUserComponent;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.mvp.contract.UserContract;
import pers.jay.wanandroid.mvp.presenter.UserPresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.RouterHelper;
import pers.jay.wanandroid.utils.SmartRefreshUtils;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.jay.wanandroid.widgets.PoemTextView;
import pers.zjc.commonlibs.util.FragmentUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class UserFragment extends BaseFragment<UserPresenter> implements UserContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.rlNavHeader)
    RelativeLayout rlNavHeader;
    @BindView(R.id.icRanking)
    ImageView icRanking;
    @BindView(R.id.tvLevel)
    TextView tvLevel;
    @BindView(R.id.tvRank)
    TextView tvRank;
    @BindView(R.id.llLevel)
    LinearLayout llLevel;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvIntegral)
    TextView tvIntegral;
    @BindView(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @BindView(R.id.tvPoem)
    PoemTextView tvPoem;
    @BindView(R.id.toolbar)
    Toolbar toolBar;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.statusView)
    MultipleStatusView statusView;
    private long mUserId = -1;
    private String mTitle;
    private int mPage = 1;
    private int mPageCount;
    private ArticleAdapter adapter;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerUserComponent //如找不到该类,请编译一下项目
                .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    /**
     * fragment点击穿透解决办法，从事件分发的角度来解决。
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.setClickable(true); //把View的click属性设为true，截断点击时间段扩散
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
        initArgs();
    }

    private void initArgs() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            killMyself();
            return;
        }
        mUserId = bundle.getLong(Const.Key.KEY_USER_ID, -1);
        mTitle = bundle.getString(Const.Key.KEY_TITLE, "");
        if (mUserId == -1) {
            showError("暂无作者信息哦~");
            return;
        }
        assert mPresenter != null;
        showLoading();
        mPresenter.loadData(mUserId, mPage);
    }

    private void initView() {
        initRefresh();
        initAppbar();
        initRv();
    }

    private void initRefresh() {
        StoreHouseHeader header = new StoreHouseHeader(mContext);
        header.initWithString("WANANDROID");
        SmartRefreshUtils.with(refreshLayout)
                .pureScrollMode()
                .setRefreshHeader(header)
                .setRefreshListener(() -> {
                    mPage = 1;
                    mPresenter.loadData(mUserId, mPage);
                });
    }

    private void initAppbar() {
        // 由于设置了沉浸式，下移一个状态栏高度
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)toolBar.getLayoutParams();
//        params.setMargins(0, BarUtils.getStatusBarHeight(), 0, 0);
//        toolBar.setLayoutParams(params);
        tvTitle.setText(mTitle);
        ivLeft.setOnClickListener(v -> killMyself());
        // navHeader
        icRanking.setVisibility(View.INVISIBLE);
        tvPoem.setVisibility(View.GONE);
        tvUserName.setText("---");
        tvIntegral.setText("积分:-");
        tvLevel.setText("等级:-");
        tvRank.setText("排行:-");

        //使用下面两个方法设置展开透明->折叠时你想要的颜色 设置收缩展开toolbar字体颜色
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        // 滑动事件 设置标题
        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                tvTitle.setAlpha(1);
                tvTitle.setText(mTitle);
                rlNavHeader.setVisibility(View.INVISIBLE);
            } else {
                tvTitle.setAlpha(0);
                tvTitle.setText("");
                rlNavHeader.setVisibility(View.VISIBLE);
            }
//            else {
//                int alpha = (int) (255 - verticalOffset / (float) appBarLayout.getTotalScrollRange() * 255);
//                toolBar.setAlpha(alpha);
//                tvTitle.setAlpha(alpha);
//                tvTitle.setText(mTitle);
//                Timber.e("verticalOffset == else, alpha=%s", alpha);
//            }
        });
    }

    private void initRv() {
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        ArmsUtils.configRecyclerView(mRecyclerView, new WrapContentLinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if (mPageCount != 0 && mPageCount == mPage + 1) {
                adapter.loadMoreEnd();
                return;
            }
            mPage++;
            assert mPresenter != null;
            mPresenter.loadData(mUserId, mPage);
        }, mRecyclerView);
        statusView.setOnRetryClickListener(v -> mPresenter.loadData(mUserId, mPage));
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
        adapter.setOnItemClickListener((adapter, view, position) -> RouterHelper.switchToWebPage(mContext, this.adapter.getData().get(position)));
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        statusView.showLoading();
    }

    @Override
    public void hideLoading() {
        statusView.showContent();
        refreshLayout.finishRefresh();
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
        if (getFragmentManager() != null) {
            FragmentUtils.pop(getFragmentManager(), true);
        }
    }

    @Override
    public void showError(String msg) {
        statusView.showError(msg);
    }

    @Override
    public void showNoNetwork() {
        statusView.showNoNetwork();
    }

    @Override
    public void showArticles(ArticleInfo data) {
        mPageCount = data.getPageCount();
        List<Article> articles = data.getDatas();
        if (articles.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (data.getCurPage() == 1) {
            adapter.replaceData(articles);
            if (data.getPageCount() == data.getCurPage()) {
                adapter.loadMoreEnd();
            }
        } else {
            adapter.addData(data.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showCoin(Coin coin) {
        ivAvatar.setVisibility(View.VISIBLE);
        tvUserName.setText(coin.getUsername());
        tvIntegral.setText(String.format("积分:%s", coin.getCoinCount()));
        tvLevel.setText(coin.getLevelStr());
        tvRank.setText(coin.getFormatRank());
    }

    @Override
    public boolean onBackPress() {
        killMyself();
        return true;
    }

    @Override
    public void onCollectSuccess(Article article, int position) {

    }

    @Override
    public void onCollectFail(Article article, int position) {
        adapter.restoreLike(position);
    }
}
