package pers.jay.wanandroid.mvp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.zjc.commonlibs.ui.BasePagerAdapter;

public class StructureFragment extends BaseFragment implements ScrollTopListener {

    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private BasePagerAdapter<String, Fragment> adapter;
    private String[] mTitles= {"体系", "导航"};

    public static Fragment newInstance() {
        return new StructureFragment();
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initTabs();
    }

    private void initTabs() {
        adapter = new BasePagerAdapter<>(getChildFragmentManager(),
                new BasePagerAdapter.PagerFragCreator<String, Fragment>() {
                    @Override
                    public Fragment createFragment(String data, int position) {
                        switch (position) {
                            case 0:
                            default:
                                return KnowledgeFragment.newInstance();
                            case 1:
                                return NavFragment.newInstance();
                        }
                    }

                    @Override
                    public String createTitle(String data) {
                        return data;
                    }
                });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                tabLayout.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        adapter.setData(Arrays.asList(mTitles));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabLayout.setTabSpaceEqual(true);
        tabLayout.setViewPager(viewPager, mTitles);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void scrollToTop() {
        if (adapter == null || viewPager == null) {
            return;
        }
        // 获取缓存的fragment引用
        Fragment fragment = adapter.getFragment(viewPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded() && fragment.getUserVisibleHint() && fragment instanceof ScrollTopListener) {
            ((ScrollTopListener)fragment).scrollToTop();
        }
    }

    @Override
    public void scrollToTopRefresh() {
        if (adapter == null || viewPager == null) {
            return;
        }
        // 获取缓存的fragment引用
        Fragment fragment = adapter.getFragment(viewPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        if (fragment.isAdded() && fragment.getUserVisibleHint() && fragment instanceof ScrollTopListener) {
            ((ScrollTopListener)fragment).scrollToTopRefresh();
        }
    }
}
