package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerWeixinComponent;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.mvp.contract.WeixinContract;
import pers.jay.wanandroid.mvp.presenter.WeixinPresenter;
import pers.jay.wanandroid.mvp.ui.adapter.TabFragmentStatePagerAdapter;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class WeixinFragment extends BaseLazyLoadFragment<WeixinPresenter>
        implements WeixinContract.View, ScrollTopListener {

    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private TabFragmentStatePagerAdapter adapter;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private FragmentActivity fragmentActivity;

    public static WeixinFragment newInstance() {
        WeixinFragment fragment = new WeixinFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerWeixinComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weixin, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(mFragments.size());
        adapter = new TabFragmentStatePagerAdapter(getChildFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        tabLayout.setViewPager(viewPager, mTitles.toArray(new String[0]));
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
    protected void lazyLoadData() {
        mPresenter.requestWxTab();
    }

    @Override
    public void scrollToTop() {
        if (mFragments.isEmpty()) {
            return;
        }
        for (Fragment fragment : mFragments) {
            if (fragment.isAdded() && fragment.getUserVisibleHint() && fragment instanceof ScrollTopListener) {
                ((ScrollTopListener)fragment).scrollToTop();
            }
        }
     }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void showData(List<Tab> data) {
        for (Tab wxTab : data) {
            TabFragment fragment = TabFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt(Const.Key.KEY_TAB_FROM_TYPE, Const.Type.TYPE_TAB_WEIXIN);
            bundle.putParcelable(Const.Key.KEY_TAB_DATA, wxTab);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            mTitles.add(NetWorkManager.htmlReplace(wxTab.getName()));
        }
        initViewPager();
        initTabLayout();
    }


}
