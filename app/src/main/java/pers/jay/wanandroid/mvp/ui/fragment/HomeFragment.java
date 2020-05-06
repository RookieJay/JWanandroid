package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.like.LikeButton;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.GlideImageLoader;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerHomeComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.mvp.contract.HomeContract;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.presenter.HomePresenter;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.mvp.ui.adapter.ArticleAdapter;
import pers.jay.wanandroid.utils.JUtils;
import pers.jay.wanandroid.utils.RvScrollTopUtils;
import pers.jay.wanandroid.utils.SmartRefreshUtils;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HomeFragment extends BaseLazyLoadFragment<HomePresenter> implements HomeContract.View, ScrollTopListener {

    Banner banner;
    Unbinder unbinder;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArticleAdapter adapter;

    private int page;
    private List<BannerImg> mBannerImgs;
    private List<Article> mArticleList = new ArrayList<>();
    private int pageCount;
    private LinearLayoutManager layoutManager;

    private Animation likeAnimation;
    private List<String> bannerUrls = new ArrayList<>();
    private List<String> bannerTitles = new ArrayList<>();

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerHomeComponent //如找不到该类,请编译一下项目
                            .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Const.Key.SAVE_INSTANCE_STATE)) {
//                lazyLoadData();
            }
        }
        initRefreshLayout();
        initScrollList();
        initBanner();
        initScaleAnimation();
    }

    private void initScaleAnimation() {
        likeAnimation = AnimationUtils.loadAnimation(JApplication.getInstance(), R.anim.anim_scale_from_center_out);
    }

    private void initScrollList() {
        assert mPresenter != null;
        adapter = new ArticleAdapter(new ArrayList<>(), ArticleAdapter.TYPE_COMMON);
        loadAnimation(AppConfig.getInstance().getRvAnim());
        layoutManager = new LinearLayoutManager(mContext);
        ArmsUtils.configRecyclerView(mRecyclerView, layoutManager);
        //分割线 1px 颜色colorPrimary
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL_LIST, 1, R.color.colorPrimary));
        mRecyclerView.setAdapter(adapter);
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

        adapter.setOnLoadMoreListener(() -> {
            if (pageCount != 0 && pageCount == page + 1) {
                adapter.loadMoreEnd();
                return;
            }
            page++;
            mPresenter.requestArticle(page);
        }, mRecyclerView);
        // 开启越界回弹
        refreshLayout.setEnableOverScrollBounce(true);
        refreshLayout.setEnableOverScrollDrag(true);
    }

    private void switchToWebPage(int position) {
        Intent intent = new Intent(mContext, X5WebActivity.class);
        Article article = adapter.getData().get(position);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_ARTICLE);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, article);
        launchActivity(intent);
    }

    private void initRefreshLayout() {
        StoreHouseHeader header = new StoreHouseHeader(mContext);
        header.initWithString("WANANDROID");
        SmartRefreshUtils.with(refreshLayout).setRefreshListener(() -> {
            if (mPresenter != null) {
                page = 0;
                mPresenter.requestHomeData();
            }
        });
        refreshLayout.setRefreshHeader(header);
        refreshLayout.setEnableAutoLoadMore(false); //是否启用列表惯性滑动到底部时自动加载更多
        refreshLayout.setDisableContentWhenRefresh(true); //是否在刷新的时候禁止列表的操作
        refreshLayout.setEnableLoadMore(false); //是否启用列表手动加载更多
    }

    private void initBanner() {
        // 用代码创建的banner无法显示指示器，换为使用布局创建
//        banner = new Banner(mContext);
        banner = (Banner)LayoutInflater.from(mContext).inflate(R.layout.layout_banner, mRecyclerView, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                UIUtils.dp2px(mContext, 200L));
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setLayoutParams(params);
        //显示圆形指示器和标题（水平显示)
        banner.setOnBannerListener(position -> {
            if (null != mBannerImgs && mBannerImgs.size() > 0) {
                Intent intent = new Intent(mContext, X5WebActivity.class);
                intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, X5WebActivity.TYPE_BANNER);
                intent.putExtra(Const.Key.KEY_WEB_PAGE_DATA, mBannerImgs.get(position));
                startActivity(intent);
            }
        });
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void showMoreArticles(ArticleInfo articleInfo) {
        this.pageCount = articleInfo.getPageCount();
        List<Article> data = articleInfo.getDatas();
        this.mArticleList.addAll(data);
        adapter.addData(data);
        adapter.loadMoreComplete();
    }

    @Override
    public void showBanner(List<BannerImg> bannerImgs) {
        Timber.e("请求到banner，当前线程"+ Thread.currentThread().getName());
        mBannerImgs = bannerImgs;
        bannerUrls.clear();
        bannerTitles.clear();
        for (BannerImg bannerImg : bannerImgs) {
            bannerUrls.add(bannerImg.getImagePath());
            bannerTitles.add(JUtils.html2String(bannerImg.getTitle()));
        }
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(bannerUrls);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(bannerTitles);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        // 解决IllegalStateException，不能通过addHeaderView重复添加子View
        // 2.6.8版本新增setHeaderView方法
        if (adapter.getHeaderLayoutCount() > 0) {
            adapter.setHeaderView(banner);
        }
        else {
            adapter.addHeaderView(banner);
        }
    }

    @Override
    public void refresh(List<Article> articleList) {
        mArticleList = articleList;
        adapter.replaceData(articleList);
    }

    @Override
    public void updateCollectStatus(boolean collect, Article item, int position) {
        for (Article article : adapter.getData()) {
            if (article.equals(item)) {
                article.setCollect(collect);
            }
        }
        adapter.notifyItemChanged(position + 1); // 存在headerView,position + 1
    }


    @Override
    public void showLoadMoreFail() {
        adapter.loadMoreFail();
    }

    @Override
    public void restoreLikeButton(int position) {
        LikeButton likeButton = (LikeButton)adapter.getViewByPosition(position, R.id.ivLike);
        if (likeButton == null) {
            Timber.e("没找到按钮");
            return;
        }
        likeButton.setLiked(!likeButton.isLiked());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addDailyPic(String url) {
        List<String> titles = new ArrayList<>(bannerTitles);
        List<String> urls = new ArrayList<>(bannerUrls);
        titles.add(0, "每日一图");
        urls.add(0, url);
        banner.update(urls, titles);

    }

    @Override
    protected void lazyLoadData() {
        showLoading();
        mPresenter.requestHomeData();
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
            lazyLoadData();
        }
    }

    /**
     * 退出登录
     */
    @Subscriber
    public void onLogout(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOG_OUT) {
            lazyLoadData();
        }
    }

    private void loadAnimation(int type) {
        adapter.openLoadAnimation(type);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Timber.e("%s 保存状态", this.getClass().getSimpleName());
        // 意外销毁时（屏幕方向切换、颜色模式改变等）保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }
}
