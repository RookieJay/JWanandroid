package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jess.arms.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.di.component.DaggerProjectComponent;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.mvp.contract.ProjectContract;
import pers.jay.wanandroid.mvp.presenter.ProjectPresenter;
import pers.jay.wanandroid.mvp.ui.adapter.TabFragmentStatePagerAdapter;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class ProjectFragment extends BaseLazyLoadFragment<ProjectPresenter>
        implements ProjectContract.View, ScrollTopListener {

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
    private List<Tab> mTabs = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentActivity = (FragmentActivity)context;
    }

    public static ProjectFragment newInstance() {
        ProjectFragment fragment = new ProjectFragment();
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerProjectComponent //如找不到该类,请编译一下项目
                               .builder()
                               .appComponent(appComponent)
                               .view(this)
                               .build()
                               .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

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
        mPresenter.requestProjectTab();
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

    @Override
    public void showData(List<Tab> data) {
        this.mTabs = data;
        for (Tab tab : data) {
            TabFragment fragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Const.Key.KEY_TAB_FROM_TYPE, Const.Type.TYPE_TAB_PROJECT);
            bundle.putParcelable(Const.Key.KEY_TAB_DATA, tab);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            mTitles.add(NetWorkManager.htmlReplace(tab.getName()));
        }
        initViewPager();
        initTabLayout();
    }

    private void initViewPager() {
        //notifyDataSetChanged()是在getCount()发生改变的时候去调用，哪里影响了getCount()，就应该再哪里调用！
        //在adt22之后，PagerAdapter对于notifyDataSetChanged()和getCount()的执行顺序是非常严格的，系统跟踪count的值，
        // 如果这个值和getCount返回的值不一致，就会抛出这个异常。所以为了保证getCount总是返回一个正确的值，
        // 那么在初始化ViewPager时，应先给Adapter初始化内容后再将该adapter传给ViewPager，如果不这样处理，
        // 在更新adapter的内容后，应该调用一下adapter的notifyDataSetChanged方法。
        // 出错代码：viewPager.setOffscreenPageLimit((int)Math.ceil(mFragments.size() / 2));
        //        adapter = new TabFragmentStatePagerAdapter(getChildFragmentManager(), mFragments);
        //        viewPager.setAdapter(adapter);
        adapter = new TabFragmentStatePagerAdapter(getChildFragmentManager(), mFragments);
        viewPager.setOffscreenPageLimit((int)Math.ceil(mFragments.size() / 2));
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        tabLayout.setViewPager(viewPager, mTitles.toArray(new String[0]));
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (mTabs.size() == 0) {
                    return;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

}
