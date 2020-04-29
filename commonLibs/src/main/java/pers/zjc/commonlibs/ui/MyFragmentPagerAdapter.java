package pers.zjc.commonlibs.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 自定义FragmentPagerAdapter，用于底部tab+viewPager切换Fragment场景
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragmentList = fragments;
    }

    /**
     * getItem是创建一个新的Fragment，但是这个方法名可能会被误认为是返回一个已经存在的Fragment。
     */
    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
