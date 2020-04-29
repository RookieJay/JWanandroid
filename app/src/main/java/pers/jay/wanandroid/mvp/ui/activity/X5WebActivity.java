package pers.jay.wanandroid.mvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.ycbjie.webviewlib.InterWebListener;
import com.ycbjie.webviewlib.X5WebChromeClient;
import com.ycbjie.webviewlib.X5WebView;
import com.ycbjie.webviewlib.X5WebViewClient;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerX5Component;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.contract.X5Contract;
import pers.jay.wanandroid.mvp.presenter.X5Presenter;
import pers.jay.wanandroid.utils.ADFilterTool;
import pers.jay.wanandroid.utils.DarkModeUtils;
import pers.jay.wanandroid.utils.JUtils;
import pers.jay.wanandroid.utils.UIUtils;
import pers.jay.wanandroid.widgets.WebViewProgress;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

public class X5WebActivity extends BaseActivity<X5Presenter> implements X5Contract.View {

    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_BANNER = 2;
    public static final int TYPE_URL = 3;

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
    @BindView(R.id.webView)
    X5WebView webView;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivForward)
    ImageView ivForward;
    @BindView(R.id.flWeb)
    FrameLayout flWeb;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;

    private String mUrl = Const.Url.WAN_ANDROID;
    private String mTitle = "";
    private WebViewProgress lv;
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
        UIUtils.setSameColorBar(true, getWindow(), getResources());
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
                    mUrl = mArticle.getLink();
                    mTitle = mArticle.getTitle();
                    break;
                case TYPE_BANNER:
                    BannerImg bannerImg = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
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
        ivRight.setOnClickListener(v -> mPresenter.collectArticle(mArticle));
        if (mArticle == null) {
            return;
        }
        ivRight.setImageResource(mArticle.isCollect() ? R.drawable.ic_like_fill : R.drawable.ic_like);
        ivRight.setVisibility(mArticle == null ? View.GONE : View.VISIBLE);
        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.scrollTo(0, 0);
                webView.flingScroll(5, 5);
            }
        });
    }

    private void initWebView() {
        webView.setEnabled(false);

        lv = new WebViewProgress(X5WebActivity.this);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        //如果不设置WebViewClient，请求会跳转系统浏览器
//        webView.setWebViewClient(new MyX5WebViewClient(webView, this));
        webView.setWebChromeClient(new MyX5WebChromeClient(webView, lv,this));
        webView.getSettings().setJavaScriptEnabled(true);
        if (DarkModeUtils.getMode(AppConfig.getInstance().getDarkModePosition()) == AppCompatDelegate.MODE_NIGHT_YES) {
            Timber.e("暗黑模式");
            webView.loadUrl("javascript:function setBgColor(){" + "document.getElementsByTagName('body')[0].style.background='#1A1714'" + "};setBgColor();");
            webView.loadUrl("javascript:function setTextColor(){document.getElementsByTagName('body')[0].style.webkitTextFillColor= '#999999'" + "};setTextColor();");
            webView.setBackground(getDrawable(R.color.black));
        }
        webView.loadUrl(mUrl);
        // 加载完成后才可以滚动
        if (lv.getProgress() == 100) {
            webView.setEnabled(true);
        }
    }

    private class MyX5WebViewClient extends X5WebViewClient {

        MyX5WebViewClient(WebView webView, Context context) {
            super(webView, context);
        }

        //重写你需要的方法即可

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return !mUrl.contains(url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String host = request.getUrl().getHost();
            if (ADFilterTool.hasAd(X5WebActivity.this, host)) {
                ToastUtils.showShort("禁止跳转其他页面");
                return true;
            }
            return false;
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

        MyX5WebChromeClient(WebView webView, WebViewProgress progress, Activity activity) {
            super(webView, activity);
            wp = progress;
            wp.setMax(100);
            wp.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dp2px(activity.getApplicationContext(), 2)));
            webView.addView(wp);
            setWebListener(new InterWebListener() {
                @Override
                public void hindProgressBar() {
                    Timber.e("hindProgressBar");
                }

                @Override
                public void showErrorView(int type) {
                    Timber.e("showErrorView");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击回退键时，不会退出浏览器而是返回网页上一页
        //        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
        //            webView.goBack();
        //            return true;
        //        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void killMyself() {
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void updateCollectStatus(boolean collect, Article article) {
        ivRight.setImageResource(collect ? R.drawable.ic_like_fill : R.drawable.ic_like);
        article.setCollect(collect);
        EventBusManager.getInstance().post(new Event<Article>(Const.EventCode.COLLECT_ARTICLE, article));
    }
}
