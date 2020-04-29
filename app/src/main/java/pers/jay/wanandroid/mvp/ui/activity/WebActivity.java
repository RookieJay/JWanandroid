package pers.jay.wanandroid.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.mvp.contract.WebContract;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.mvp.presenter.WebPresenter;
import pers.jay.wanandroid.utils.UIUtils;
import pers.jay.wanandroid.utils.WebViewClient;
import pers.jay.wanandroid.widgets.CustomWebView;
import pers.zjc.commonlibs.util.ToastUtils;

import static android.view.KeyEvent.KEYCODE_BACK;
import static com.jess.arms.utils.Preconditions.checkNotNull;

public class WebActivity extends BaseActivity<WebPresenter> implements WebContract.View {

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
    CustomWebView webView;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivRefresh)
    ImageView ivRefresh;
    @BindView(R.id.ivForward)
    ImageView ivForward;

    private String mUrl = Const.Url.WAN_ANDROID;
    private String mTitle = "";

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
//        DaggerWebComponent //如找不到该类,请编译一下项目
//                           .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_web; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        UIUtils.setSameColorBar(true, getWindow(), getResources());
        initParams();
        initTitle();
        initWebView();
        initNavigation();
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
                    Article article = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    if (article == null) {
                        showMessage("参数错误");
                        killMyself();
                        return;
                    }
                    mUrl = article.getLink();
                    mTitle = article.getTitle();
                    break;
                case TYPE_BANNER:
                    BannerImg bannerImg = intent.getParcelableExtra(Const.Key.KEY_WEB_PAGE_DATA);
                    if (bannerImg == null) {
                        showMessage("参数错误");
                        killMyself();
                        return;
                    }
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

    private void initNavigation() {
        ivBack.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        ivForward.setOnClickListener(v -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });
        ivRefresh.setOnClickListener(v -> webView.reload());
    }

    private void initTitle() {
        ivLeft.setImageResource(R.mipmap.ic_arrow_back_white_24dp);
        // 跑马灯必须加
        tvTitle.setSelected(true);
        tvTitle.setFocusable(true);
        tvTitle.setFocusableInTouchMode(true);
        tvTitle.setText(mTitle);
        ivRight.setImageResource(R.drawable.ic_more_vert);
        ivLeft.setOnClickListener(v -> killMyself());
        ivRight.setOnClickListener(v -> showPopMenu());
    }

    private void showPopMenu() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //设置垂直滚动条
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setScrollBarSize(UIUtils.dp2px(this, 20));
        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
//        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient(WebActivity.this, mUrl));
        webView.loadUrl(mUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击回退键时，不会退出浏览器而是返回网页上一页
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        finish();
    }

    @Override
    public void updateCollectStatus(boolean collect, Article article) {
        ivRight.setImageResource(collect ? R.drawable.ic_like_fill : R.drawable.ic_like);
        article.setCollect(collect);
    }
}
