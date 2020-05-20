package pers.jay.wanandroid.mvp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.PermissionUtil;
import com.jinrishici.sdk.android.view.JinrishiciTextViewConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxPhotoTool;

import de.hdodenhof.circleimageview.CircleImageView;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.CookiesManager;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerContainerComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.mvp.contract.ContainerContract;
import pers.jay.wanandroid.mvp.presenter.ContainerPresenter;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.activity.SearchActivity;
import pers.jay.wanandroid.mvp.ui.activity.SettingsActivity;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
import pers.jay.wanandroid.utils.UIUtils;
import pers.jay.wanandroid.utils.rx.RxScheduler;
import pers.jay.wanandroid.widgets.PoemTextView;
import pers.zjc.commonlibs.constant.PermissionConstants;
import pers.zjc.commonlibs.ui.BasePagerAdapter;
import pers.zjc.commonlibs.util.ImageUtils;
import pers.zjc.commonlibs.util.PermissionUtils;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.TimeUtils;
import pers.zjc.commonlibs.util.UriUtils;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;
import static com.vondear.rxtool.RxPhotoTool.GET_IMAGE_BY_CAMERA;
import static com.vondear.rxtool.RxPhotoTool.GET_IMAGE_FROM_PHONE;

/**
 * @author ZJC
 */
public class ContainerFragment extends BaseFragment<ContainerPresenter>
        implements ContainerContract.View {

    private static final long INTERVAL_DOUBLE_CLICK = 1000L;
    private static final int TYPE_LOAD_LOCAL_IMAGE = 1;
    private static final int TYPE_ACCESS_LOCAL_IMAGE = 2;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vpContainer)
    ViewPager viewPager;
    @BindView(R.id.bottomNav)
    BottomNavigationView bottomNav;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    private PoemTextView tvPoem;

    @Inject
    AppConfig appConfig;
    @Inject
    CookiesManager cookiesManager;

    private ImageView ivRank;
    private TextView tvLevel;
    private TextView tvIntegral;
    private TextView tvUserName;
    private TextView tvRank;
    private CircleImageView ivAvatar;
    private int mStartType = 0;
    private String[] mTitles = { "首页", "知识体系", "公众号", "导航", "项目" };
    private BasePagerAdapter<String, Fragment> fragmentPagerAdapter;

    private long firstClick = 0L;
    private MainActivity mActivity;
    private int coinCount;
    private TextView tvCoinCount;
    private Coin myCoin;
    private View headerView;
    private View rlNavHeader;

    public static ContainerFragment newInstance() {
        ContainerFragment fragment = new ContainerFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity)context;
        }
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerContainerComponent //如找不到该类,请编译一下项目
                                 .builder()
                                 .appComponent(appComponent)
                                 .view(this)
                                 .build()
                                 .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UIUtils.setSameColorBar(true, mActivity.getWindow(), getResources());
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Const.Key.SAVE_INSTANCE_STATE)) {

            }
        }
        initView();
        loadMyCoin();
        // 诗词
//        mPresenter.loadPoem();
        tvPoem.setConfig(new JinrishiciTextViewConfig());
    }

    private void loadMyCoin() {
        tvLevel.setText(String.format("等级:%s", "--"));
        tvIntegral.setText(String.format("id:%s", "--"));
        tvRank.setText(String.format("排行:%s", "--"));
        if (isLogin()) {
            mPresenter.loadCoin();
        }
        else {
            tvUserName.setText(getResources().getString(R.string.hint_user_name));
        }
    }

    private void initView() {
        initNavigationView();
        initToolbar();
        initFloatingButton();
        initBottomBav();
        initViewPager();
    }

    private void initNavigationView() {
        headerView = navigationView.getHeaderView(0);
        rlNavHeader = headerView.findViewById(R.id.rlNavHeader);
        ivRank = headerView.findViewById(R.id.icRanking);
        tvLevel = headerView.findViewById(R.id.tvLevel);
        tvIntegral = headerView.findViewById(R.id.tvIntegral);
        tvUserName = headerView.findViewById(R.id.tvUserName);
        tvRank = headerView.findViewById(R.id.tvRank);
        ivAvatar = headerView.findViewById(R.id.ivAvatar);
        tvPoem = headerView.findViewById(R.id.tvPoem);
        // 隐藏掉 转到刷新header
        tvPoem.setVisibility(View.GONE);
        ivAvatar.setBorderColor(ContextCompat.getColor(mContext, R.color.base_bg_color));
        ivAvatar.setBorderWidth(5);
        ivAvatar.setOnClickListener(v -> openGallery());
        tvCoinCount = (TextView)navigationView.getMenu().getItem(0).getActionView();
        tvCoinCount.setGravity(Gravity.CENTER_VERTICAL);
        //设置图片为本身的颜色
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_integral:
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.Key.KEY_COIN_COUNT, coinCount);
                    Fragment fragment = MyCoinFragment.newInstance();
                    fragment.setArguments(bundle);
                    mActivity.switchFragment(fragment);
                    break;
                case R.id.nav_share:
                    mActivity.switchFragment(MySharesFragment.newInstance());
                    break;
                case R.id.nav_collection:
                    mActivity.switchFragment(CollectionFragment.newInstance());
                    break;
                case R.id.nav_todo:
                    mActivity.switchFragment(TodoTabFragment.newInstance());
                    //                    launchActivity(new Intent(mContext, TodoTabFragment.class));
                    break;
                case R.id.nav_settings:
                    launchActivity(new Intent(mContext, SettingsActivity.class));
                    break;
                case R.id.nav_about_us:
                    Intent intent = new Intent(mActivity, X5WebActivity.class);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_URL);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_URL, Const.Url.ABOUT_US);
                    intent.putExtra(Const.Key.KEY_WEB_PAGE_TITLE,
                            getResources().getString(R.string.drawer_about_us));
                    launchActivity(intent);
                    break;
                case R.id.nav_logout:
                    UIUtils.createConfirmDialog(mContext, "您确认要退出登录吗？", true,
                            (dialog, which) -> mPresenter.logout(), null).show();

                    break;
                default:
                    break;
            }
            return true;
        });
        //        setDrawerToggle();
        ivRank.setOnClickListener(v -> switchToRankPage());
        tvUserName.setOnClickListener(v -> {
            if (!isLogin()) {
                mActivity.switchFragment(LoginFragment.newInstance());
            }
        });
        configLogoutButton();
        // 加载头像
        if (PermissionUtils.isGranted(PermissionConstants.STORAGE)) {
            loadLocalAvatar();
        } else {
            requestStoragePermissions(TYPE_LOAD_LOCAL_IMAGE);
        }
    }

    /**
     * 初始化本地储存的头像并高斯模糊
     */
    private void loadLocalAvatar() {
        if (StringUtils.isEmpty(appConfig.getAvatar())) {
            Glide.with(ivAvatar.getContext()).load(R.drawable.ic_avatar).into(ivAvatar);
        } else {
            Glide.with(ivAvatar.getContext()).load(appConfig.getAvatar()).into(ivAvatar);
            blurBackground(new File(appConfig.getAvatar()));
        }
    }

    private void openGallery() {
        if (!appConfig.isLogin()) {
            mActivity.switchFragment(LoginFragment.newInstance());
            return;
        }
        // 申请权限
        requestStoragePermissions(TYPE_ACCESS_LOCAL_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            switch (requestCode) {
                case GET_IMAGE_BY_CAMERA:
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    if (bitmap == null) {
                        return;
                    }
                    Glide.with(ivAvatar.getContext()).load(bitmap).into(ivAvatar);
                    break;
                case GET_IMAGE_FROM_PHONE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        File file = UriUtils.uri2File(uri);
                        if (file == null) {
                            return;
                        }
                        if (!ImageUtils.isImage(file)) {
                            showMessage("请选择图片文件");
                            return;
                        }
                        Glide.with(ivAvatar.getContext()).load(uri).into(ivAvatar);
                        appConfig.setAvatar(file.getAbsolutePath());
                        blurBackground(file);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 注意：要针对Android 10(Q)储存权限适配：取消对READ_EXTERNAL_STORAGE 和 WRITE_EXTERNAL_STORAGE两个权限的申请。
     * 并替换为新的媒体特定权限。
     * @param type
     */
    private void requestStoragePermissions(int type) {
//        PermissionUtils.permission(PermissionConstants.STORAGE).callback(
//                new PermissionUtils.SimpleCallback() {
//                    @Override
//                    public void onGranted() {
//                        switch (type) {
//                            case TYPE_LOAD_LOCAL_IMAGE:
//                                loadLocalAvatar();
//                                break;
//                            case TYPE_ACCESS_LOCAL_IMAGE:
//                                // 打开本地图库
//                                RxPhotoTool.openLocalImage(ContainerFragment.this);
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onDenied() {
//                        showMessage("拒绝存储权限将无法正常使用app,可能导致崩溃,请前往系统设置打开存储权限");
//                    }
//                }).request();
        PermissionUtil.RequestPermission requestPermission = new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                switch (type) {
                    case TYPE_LOAD_LOCAL_IMAGE:
                        loadLocalAvatar();
                        break;
                    case TYPE_ACCESS_LOCAL_IMAGE:
                        // 打开本地图库
                        RxPhotoTool.openLocalImage(ContainerFragment.this);
                        break;
                }
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                showMessage("拒绝存储权限将无法正常使用app,可能导致崩溃");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                showMessage("您已拒绝存储权限，将无法正常使用app,请前往系统设置打开存储权限");
            }
        };
        RxErrorHandler handler = RxErrorHandler.builder().with(mContext).responseErrorListener(
                (context, t) -> Timber.e(t.getMessage())).build();
        PermissionUtil.requestPermission(requestPermission, new RxPermissions(this), handler,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void blurBackground(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        // 被观察者
        Observable<Bitmap> observable = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                Bitmap source = ImageUtils.getBitmap(file);
                if (source == null) {
                    emitter.onError(new Throwable("Bitmap转换失败"));
                    return;
                }
                // 先按照1/8进行缩放，然后再进行模糊
                Bitmap blurBm = UIUtils.rsBlur(mContext, source, 20, 1f / 8f);
                emitter.onNext(blurBm);
            }
        });
        // 观察者
        Observer<Bitmap> observer = new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap blurredBm) {
                if (rlNavHeader == null) {
                    return;
                }
                rlNavHeader.setBackground(new BitmapDrawable(getResources(), blurredBm));
            }

            @Override
            public void onError(Throwable e) {
                showMessage(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        };
        // 切换线程并订阅
        observable.compose(RxScheduler.Obs_io_main())
                  .subscribe(observer);
    }

    private void configLogoutButton() {
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(isLogin());
    }

    private void switchToRankPage() {
        Fragment fragment = RankFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Const.Key.KEY_MY_COIN, myCoin);
        fragment.setArguments(bundle);
        mActivity.switchFragment(fragment);
    }

    private void setDrawerToggle() {
        //通过actionbardrawertoggle将toolbar与drawablelayout关联起来
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                R.string.hint_user_name, R.string.hint_user_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //可以重新侧滑方法,该方法实现侧滑动画,整个布局移动效果
                //获取mDrawerLayout中的第一个子布局，也就是布局中的RelativeLayout
                //获取抽屉的view
                View mContent = drawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;
                float endScale = 0.8f + scale * 0.2f;
                float startScale = 1 - 0.3f * scale;

                //设置左边菜单滑动后的占据屏幕大小
                drawerView.setScaleX(startScale);
                drawerView.setScaleY(startScale);
                //设置菜单透明度
                drawerView.setAlpha(0.6f + 0.4f * (1 - scale));

                //设置内容界面水平和垂直方向偏转量
                //在滑动时内容界面的宽度为 屏幕宽度减去菜单界面所占宽度
                mContent.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
                //设置内容界面操作无效（比如有button就会点击无效）
                mContent.invalidate();
                //设置右边菜单滑动后的占据屏幕大小
                mContent.setScaleX(endScale);
                mContent.setScaleY(endScale);
            }
        };
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }

    private void initToolbar() {
        Glide.with(mContext).load(R.drawable.ic_menu).into(ivLeft);
        Glide.with(mContext).load(R.drawable.ic_search).into(ivRight);
        setToolbar(bottomNav.getMenu().getItem(0).getTitle().toString());
        ivLeft.setOnClickListener(v -> toggleDrawer());
        ivRight.setOnClickListener(v -> switchToSearchPage());
        //        toggleDrawer();
    }

    private void toggleDrawer() {
        drawerLayout.openDrawer(navigationView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(mActivity, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        toggle.onDrawerOpened(drawerLayout);
        toggle.syncState();
    }

    private void switchToSearchPage() {
        launchActivity(new Intent(mContext, SearchActivity.class));
    }

    private void initBottomBav() {
        // 防止其他用到该颜色值的控件，都变成透明：使用mutate()方法使该控件状态不定，这样不定状态的控件就不会共享自己的状态了。
        //        bottomNav.getBackground().mutate().setAlpha(5);
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            setToolbar(menuItem.getTitle().toString());
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.nav_wenda:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.nav_public:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.nav_structure:
                    viewPager.setCurrentItem(3);
                    break;
                case R.id.navigation_project:
                    viewPager.setCurrentItem(4);
                    break;
                default:
                    break;
            }
            return false;
        });
        bottomNav.setOnNavigationItemReselectedListener(menuItem -> slideToTop(true));
    }


    private void initFloatingButton() {
        fabTop.setOnClickListener(v -> slideToTop(false));
    }

    private void slideToTop(boolean refresh) {
        int pos = viewPager.getCurrentItem();
        Fragment fragment = fragmentPagerAdapter.getFragment(pos);
        if (fragment instanceof ScrollTopListener) {
            if (refresh) {
                ((ScrollTopListener)fragment).scrollToTopRefresh();
            }
            ((ScrollTopListener)fragment).scrollToTop();
        }
    }

    private void initViewPager() {
        // 按需设置viewPager预加载fragment数量，此处有5个界面，设置预加载4个，结合Fragment的懒加载，只预加载视图，不加载数据
        viewPager.setOffscreenPageLimit(4);
        fragmentPagerAdapter = new BasePagerAdapter<>(getFragmentManager(),
                new BasePagerAdapter.PagerFragCreator<String, Fragment>() {
                    @Override
                    public Fragment createFragment(String data, int position) {
                        return createMainFragments(position);
                    }

                    @Override
                    public String createTitle(String data) {
                        return data;
                    }
                });
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                MenuItem menuItem = bottomNav.getMenu().getItem(i);
                tvTitle.setText(menuItem.getTitle());
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        // 绑定数据
        fragmentPagerAdapter.setData(Arrays.asList(mTitles));
    }

    private Fragment createMainFragments(int position) {
        switch (position) {
            case 0:
            default:
                return HomeFragment.newInstance();
            case 1:
                return QAFragment.newInstance();
            case 2:
                return WeixinFragment.newInstance();
            case 3:
                return StructureFragment.newInstance();
            case 4:
                return ProjectFragment.newInstance();
        }
    }

    public void setToolbar(String title) {
        checkNotNull(title);
        tvTitle.setText(title);
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
        ArmsUtils.exitApp();
    }

    @Override
    public boolean onBackPress() {
        long cur = TimeUtils.getNowMills();
        if (cur - firstClick > INTERVAL_DOUBLE_CLICK) {
            showMessage(getString(R.string.back_tips));
        }
        else {
            killMyself();
        }
        firstClick = cur;
        return true;
    }

    /**
     * Token过期,重新登录
     */
//    @Subscriber
//    public void onTokenExpiredEvent(Event event) {
//        if (null != event) {
//            if (event.getEventCode() == Const.EventCode.LOGIN_EXPIRED && mContext.equals(
//                    ActivityUtils.getTopActivity())) {
//                showMessage(getString(R.string.error_login_expired));
//                if (mContext instanceof MainActivity) {
//                    mActivity.switchFragment(LoginFragment.newInstance());
//                }
//            }
//        }
//    }

    /**
     * 登录成功
     */
    @Subscriber
    public void onLoginSuccess(Event event) {
        if (null != event && event.getEventCode() == Const.EventCode.LOGIN_SUCCESS) {
            showMessage("登录成功");
            configLogoutButton();
            loadLocalAvatar();
            mPresenter.loadCoin();
        }
    }

    @Override
    public void showCoin(Coin data) {
        this.myCoin = data;
        tvUserName.setText(appConfig.getUserName());
        tvLevel.setText(data.getLevelStr());
        tvIntegral.setText(data.getIdStr());
        tvRank.setText(data.getFormatRank());
        this.coinCount = data.getCoinCount();
        tvCoinCount.setText(String.valueOf(coinCount));
    }

    @Override
    public void showLogoutSuccess() {
        tvLevel.setText(String.format("等级:%s", "--"));
        tvIntegral.setText(String.format("id:%s", "--"));
        tvRank.setText(String.format("排行:%s", "--"));
        tvUserName.setText(getResources().getString(R.string.hint_user_name));
        tvCoinCount.setText("");
        rlNavHeader.setBackground(ContextCompat.getDrawable(mContext, R.color.colorPrimary));
        appConfig.clear();
        loadLocalAvatar();
        configLogoutButton();
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.LOG_OUT, null));
    }

    @Override
    public void showPoem(String content) {
        tvPoem.setText(content);
    }

    private boolean isLogin() {
        return appConfig.isLogin();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // 意外销毁时（屏幕方向切换、颜色模式改变等）保存状态
        outState.putBoolean(Const.Key.SAVE_INSTANCE_STATE, true);
        super.onSaveInstanceState(outState);
    }

    public void switchToHome() {
        viewPager.setCurrentItem(1);
    }
}
