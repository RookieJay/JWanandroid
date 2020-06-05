package pers.jay.wanandroid.mvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.ycbjie.webviewlib.InterWebListener;
import com.ycbjie.webviewlib.X5WebChromeClient;
import com.ycbjie.webviewlib.X5WebView;
import com.ycbjie.webviewlib.X5WebViewClient;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.di.component.DaggerX5Component;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.contract.X5Contract;
import pers.jay.wanandroid.mvp.presenter.X5Presenter;
import pers.jay.wanandroid.utils.ADFilterTool;
import pers.jay.wanandroid.utils.JUtils;
import pers.jay.wanandroid.utils.UIUtils;
import pers.jay.wanandroid.widgets.CollapsingWebView;
import pers.jay.wanandroid.widgets.ScrollWebView;
import pers.jay.wanandroid.widgets.WebViewProgress;
import pers.zjc.commonlibs.util.BarUtils;
import pers.zjc.commonlibs.util.SizeUtils;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

public class X5WebActivity extends BaseActivity<X5Presenter> implements X5Contract.View {

    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_BANNER = 2;
    public static final int TYPE_URL = 3;

    @BindView(R.id.clLayout)
    CoordinatorLayout clLayout;
    @BindView(R.id.webContainer)
    FrameLayout webContainer;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    LikeButton ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.webView)
    CollapsingWebView webView;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
//    @BindView(R.id.nsvWeb)
//    NestedScrollView nsvWeb;
//    @BindView(R.id.wvp)
    WebViewProgress wvp;

    private String mUrl = Const.Url.WAN_ANDROID;
    private String mTitle = "";
    private Article mArticle;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerX5Component //如找不到该类,请编译一下项目
                          .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_x5_web;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                    .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColor(R.color.colorPrimary)
                    .init();
        initParams();
        initTitle();
        initWebView();
    }

    private void initParams() {
        Intent intent = getIntent();
        if (null != intent) {
            int startType = intent.getIntExtra(Const.Key.KEY_WEB_PAGE_TYPE, 0);
            switch (startType) {
                case 0:
                    ToastUtils.showShort("非法参数");
                    killMyself();
                    break;
                case TYPE_ARTICLE:
                    mArticle = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    assert mArticle != null;
                    mUrl = mArticle.getLink();
                    mTitle = mArticle.getTitle();
                    break;
                case TYPE_BANNER:
                    BannerImg bannerImg = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    assert bannerImg != null;
                    mUrl = bannerImg.getUrl();
                    mTitle = bannerImg.getTitle();
                    break;
                case TYPE_URL:
                default:
                    mUrl = intent.getStringExtra(Const.Key.KEY_WEB_PAGE_URL);
                    mTitle = intent.getStringExtra(Const.Key.KEY_WEB_PAGE_TITLE);
                    break;
            }
        }
    }

    private void initTitle() {
        ivLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        // 跑马灯必须加
        tvTitle.setSelected(true);
        tvTitle.setFocusable(true);
        tvTitle.setFocusableInTouchMode(true);
        tvTitle.setText(JUtils.html2String(mTitle));
        ivLeft.setOnClickListener(v -> killMyself());
        ivClose.setOnClickListener(v -> finish());
        if (mArticle == null) {
            return;
        }
        ivRight.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                mPresenter.collectArticle(mArticle);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                mPresenter.collectArticle(mArticle);
            }
        });
        ivRight.setLiked(mArticle.isCollect());
        ivRight.setVisibility(mArticle == null ? View.GONE : View.VISIBLE);
        fabTop.setOnClickListener(v -> webView.pageUp(true));
        // 进度条上移到状态栏顶部
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)wvp.getLayoutParams();
//        int statusHeight = BarUtils.getStatusBarHeight();
//        params.setMargins(0, -statusHeight, 0, 0);
    }

    private void initWebView() {
        // 使用webview的时候，不在xml里面声明，而是直接代码new个对象，传入application context防止activity引用滥用.
        // 90%的webview内存泄漏的问题便得以解决.
        webView = new CollapsingWebView(this);
        webContainer.addView(webView);
        wvp = new WebViewProgress(this);
        webContainer.addView(wvp, ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(2f));
        webView.setEnabled(false);

        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new MyX5WebViewClient(webView, this));
        webView.setWebChromeClient(new MyX5WebChromeClient(webView, wvp,this));

        // Android Q及以上开启软硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webView.setOpenLayerType(true);
        }

        webView.loadUrl(mUrl);
        // 加载完成后才可以滚动
        if (wvp.getProgress() == 100) {
            webView.setEnabled(true);
        }
        webView.setOnScrollChangeListener(new X5WebView.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
//                if (t - oldt > 0) {
//                    Timber.e("下滑");
//                } else if (t - oldt < 0){
//                    Timber.e("上滑");
//                }
            }
        });
    }

    private class MyX5WebViewClient extends X5WebViewClient {

        MyX5WebViewClient(WebView webView, Context context) {
            super(webView, context);
        }

        /**
         * url重定向会执行此方法以及点击页面某些链接也会执行此方法
         * android 7.0系统以上 已经摒弃了
         * @param view
         *            当前webview
         * @param url
         *            即将重定向的url
         * @return true:表示当前url已经加载完成，即使url还会重定向都不会再进行加载 false 表示此url默认由系统处理，该重定向还是重定向，直到加载完成
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 非标准网页不加载
            if (!url.startsWith("http:") && !url.startsWith("https:")){
                return true;
            }
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String host = request.getUrl().getHost();
            String url = request.getUrl().toString();
            String schema = request.getUrl().getScheme();
//            Timber.e("host:%s \n url:%s \n mUrl:%s", host, url, mUrl);
            // 非标准网页不加载
            if (!StringUtils.equals(schema, "http") && !StringUtils.equals(schema, "https")) {
                return true;
            }
            if (ADFilterTool.hasAd(X5WebActivity.this, url)) {
                ToastUtils.showShort("禁止跳转其他页面");
                return true;
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView,
                                                          WebResourceRequest request) {
            // 方法一：根据已知访问站点白名单过滤
            //        boolean hasWhiteSite = false;
            String host = request.getUrl().getHost();
            //        for (String whiteSite : whitelist) {
            //            if (host != null && host.contains(whiteSite)) {
            //                hasWhiteSite = true;
            //            }
            //        }
            //        if (hasWhiteSite) {
            //            return super.shouldInterceptRequest(view, request);
            //        }
            //        else {
            //            return new WebResourceResponse(null, null, null);
            //        }
            // 方法二：使用AdBlock的通配符过滤（黑名单）
            if (ADFilterTool.hasAd(X5WebActivity.this, host)) {
                return new WebResourceResponse(null, null, null);
            }
            else {
                return super.shouldInterceptRequest(webView, request);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private static class MyX5WebChromeClient extends X5WebChromeClient {

        private WebViewProgress wp;

        MyX5WebChromeClient(WebView webView, WebViewProgress progressView, Activity activity) {
            super(webView, activity);
            wp = progressView;
            setWebListener(new InterWebListener() {
                @Override
                public void hindProgressBar() {
                    Timber.e("hindProgressBar");
                }

                @Override
                public void showErrorView(int type) {
                    Timber.e("showErrorView");
                    webView.goBack();
                    webView.stopLoading();
                }

                @Override
                public void startProgress(int newProgress) {
                    Timber.e("startProgress");
                }

                @Override
                public void showTitle(String title) {
                    Timber.e("showTitle");
                }
            });
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            wp.setProgress(newProgress);
        }
    }

    @Override
    public void showMessage(@NonNull String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void killMyself() {
        if (webView == null) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void updateCollectStatus(boolean collect, Article article) {
        article.setCollect(collect);
        EventBusManager.getInstance().post(new Event<Article>(Const.EventCode.COLLECT_ARTICLE, article));
    }

    @Override
    public void onBackPressed() {
        killMyself();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
        // 在manifest.xml中设置android:process=":remote" 为独立进程，结束时杀掉进程
//        System.exit(0);
    }
}
