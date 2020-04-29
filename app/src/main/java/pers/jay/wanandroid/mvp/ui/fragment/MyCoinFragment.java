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

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerMyCoinComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.CoinHistory;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;
import pers.jay.wanandroid.mvp.presenter.MyCoinPresenter;
import pers.jay.wanandroid.mvp.ui.adapter.MyCoinAdapter;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.widgets.DashboardView;
import pers.zjc.commonlibs.util.FragmentUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class MyCoinFragment extends BaseFragment<MyCoinPresenter> implements MyCoinContract.View {

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
    @BindView(R.id.dvCoin)
    DashboardView dvCoin;
    @BindView(R.id.rlHead)
    RelativeLayout rlHead;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private MyCoinAdapter adapter;
    private int pageCount;
    private int page;

    private int maxCoin = 100;
    private int myCoin = 100;

    public static MyCoinFragment newInstance() {
        return new MyCoinFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMyCoinComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_appbar_recyclerview, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setView();
        mPresenter.loadMyCoin(1);
        mPresenter.loadRank(1);
    }

    private void setView() {
        initToolbar();
        initRecyclerView();
        fabTop.setOnClickListener(v -> scrollToTop());
    }

    private void initRecyclerView() {
        adapter = new MyCoinAdapter(R.layout.item_my_coin, new ArrayList<>());
        ArmsUtils.configRecyclerView(mRecyclerView, new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            if ((pageCount != 1 && pageCount == page + 1)) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.loadMyCoin(page);
        }, mRecyclerView);
    }

    private void initToolbar() {
        tvTitle.setText("我的积分");
        toolbarLeft.setOnClickListener(v -> killMyself());
        rlHead.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (null != bundle) {
            myCoin = bundle.getInt(Const.Key.KEY_COIN_COUNT);
            if (myCoin != 0) {
//                loadCoinAnim();
            }
        }
    }

    private void loadCoinAnim() {
        dvCoin.setProgress(maxCoin, myCoin);
//        ValueAnimator animator = ValueAnimator.ofFloat(0, myCoin);
//        animator.setDuration(4000);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float current = (float) animation.getAnimatedValue();
//
//            }
//        });
//        animator.start();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
    public boolean onBackPress() {
        killMyself();
        return true;
    }

    public void scrollToTop() {
        RvScrollTopUtils.smoothScrollTop(mRecyclerView);
    }

    @Override
    public void showData(PageInfo<CoinHistory> info) {
        pageCount = info.getPageCount();
        List<CoinHistory> coins = info.getDatas();
        if (coins.isEmpty()) {
            adapter.loadMoreEnd();
            return;
        }
        if (info.getCurPage() == 0) {
            adapter.replaceData(info.getDatas());
        }
        else {
            adapter.addData(info.getDatas());
            adapter.loadMoreComplete();
        }
    }

    @Override
    public void showRank(Coin maxCoin) {
        this.maxCoin = maxCoin.getCoinCount();
        loadCoinAnim();
    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            mPresenter.loadMyCoin(page);
        }
    }

}
