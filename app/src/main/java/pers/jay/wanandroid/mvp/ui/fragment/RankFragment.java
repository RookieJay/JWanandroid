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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerRankComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.mvp.contract.RankContract;
import pers.jay.wanandroid.mvp.presenter.RankPresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.adapter.RankAdapter;
import pers.jay.wanandroid.utils.RouterHelper;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.widgets.DashboardView;
import pers.zjc.commonlibs.util.FragmentUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class RankFragment extends BaseFragment<RankPresenter> implements RankContract.View {

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
    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.dvCoin)
    DashboardView dvCoin;

    private int pageCount;

    private RankAdapter adapter;
    private List<Coin> mData = new ArrayList<>();
    private int page = 1;
    private Coin myCoin;
    private Coin maxCoin;

    public static RankFragment newInstance() {
        return new RankFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerRankComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appbar_recyclerview, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initView();
        initArgs();
    }

    private void initArgs() {
        Bundle bundle = getArguments();
        if (null == bundle) {
            return;
        }
        myCoin = bundle.getParcelable(Const.Key.KEY_MY_COIN);
        if (null == myCoin) {
            mPresenter.loadMyCoin();
        } else {
            mPresenter.loadRank(page);
        }
    }

    private void initView() {
        initToolbar();
        initRecyclerView();
        fabTop.setOnClickListener(v -> scrollToTop());
    }

    private void initRecyclerView() {
        adapter = new RankAdapter(R.layout.item_rank, new ArrayList<>());
        ArmsUtils.configRecyclerView(mRecyclerView, new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 1 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadRank(page);
        }, mRecyclerView);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            Coin coin  = this.adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.tvUser:
                    Article article = new Article();
                    article.setAuthor(coin.getUsername());
                    article.setUserId(coin.getUserId());
                    RouterHelper.switchToUserPage((MainActivity)getActivity(), article);
                    break;
                default:
                    break;
            }
        });
    }

    private void initToolbar() {
        tvTitle.setText("积分排行榜");
        toolbarLeft.setOnClickListener(v -> killMyself());
        rlHead.setVisibility(View.VISIBLE);
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
    public void showData(PageInfo<Coin> info) {
        pageCount = info.getPageCount();
        List<Coin> coins = info.getDatas();
        if (coins.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (info.getCurPage() == 1) {
            mData = info.getDatas();
            adapter.replaceData(mData);
            // 积分进度
            maxCoin = mData.get(0);
            dvCoin.setProgress(maxCoin.getCoinCount(), myCoin.getCoinCount());
        } else {
            mData.addAll(info.getDatas());
            adapter.addData(info.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showCoin(Coin data) {
        this.myCoin = data;
        mPresenter.loadRank(page);
    }

    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public boolean onBackPress() {
        killMyself();
        return true;
    }

    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            mPresenter.loadMyCoin();
        }
    }
}
