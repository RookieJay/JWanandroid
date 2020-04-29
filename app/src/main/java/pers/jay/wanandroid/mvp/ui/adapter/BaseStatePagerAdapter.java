package pers.jay.wanandroid.mvp.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class BaseStatePagerAdapter<T, F extends Fragment> extends FragmentStatePagerAdapter {

    /* 数据集合 */
    private List<T> mData;

    private PagerFragCreator<T, F> mCreator;

    public BaseStatePagerAdapter(FragmentManager fm, PagerFragCreator<T, F> creator) {
        super(fm);
        this.mCreator = creator;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int i) {
        return mCreator.createFragment();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    public interface PagerFragCreator<T, F> {
        F createFragment();
        String createTitle(T data);
    }
}
