package pers.jay.wanandroid.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pers.jay.wanandroid.model.Tab;

/**
 * 在FragmentPagerAdapter的instantiateItem方法（这个方法会在ViewPager滑动状态变更时调用）中，
 * 每个position所对应的Fragment只会添加一次到FragmentManager里面，也就是说，我们在Adapter中重写的getItem方法，
 * 它的参数position不会出现两次相同的值。
 *
 * 当Fragment被添加时，会给这个Fragment指定一个根据itemId来区分的tag，而这个itemId就是根据getItemId方法来获取的，
 * 默认就是当前页面的索引值。
 */
public class TabFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    // 保存一份 Fragment 的引用
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private List<Tab> mData = new ArrayList<>();
    private FragmentCreator mCreator;

    public TabFragmentStatePagerAdapter(FragmentManager fm, FragmentCreator creator) {
        super(fm);
        this.mCreator = creator;
    }

    @Override
    public Fragment getItem(int i) {
        return mCreator.createFragment(mData.get(i), i);
    }

    /**
     * 这个方法，在重建也会被回调（参考上述源码）。
     * 在instantiateItem方法中，我们重写的getItem方法竟然不是每次都会被调用的！
     * 它会先判断FragmentManager是否已添加了目标Fragment（findFragmentByTag），如果已经添加了的话，就会把它取出来并重新关联上，
     * 而getItem方法就不会被调用了。
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<Tab> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mCreator.createTitle(mData.get(position));
    }

    public interface FragmentCreator {

        Fragment createFragment(Tab data, int position);

        String createTitle(Tab data);
    }
}
