package pers.zjc.commonlibs.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/**
 * 在FragmentPagerAdapter的instantiateItem方法（这个方法会在ViewPager滑动状态变更时调用）中，
 *
 * 每个position所对应的Fragment只会添加一次到FragmentManager里面，也就是说，我们在Adapter中重写的getItem方法，
 * 它的参数position不会出现两次相同的值。
 *
 * 当Fragment被添加时，会给这个Fragment指定一个根据itemId来区分的tag，而这个itemId就是根据getItemId方法来获取的，
 * 默认就是当前页面的索引值。
 *
 * 关键点就一点，getItem 这个方法不是 get Fragment，其实称之为 create Fragment更为合适。
 * 原理参考鸿洋文章：https://mp.weixin.qq.com/s/MOWdbI5IREjQP1Px-WJY1Q
 *
 * @param <T> 数据源类型
 * @param <F> Fragment
 */
public class BasePagerAdapter<T, F extends Fragment> extends FragmentPagerAdapter {

    // 数据集合
    private List<T> mData;

    // Fragment和title的构建器
    private PagerFragCreator<T, F> mCreator;

    // 保存一份 Fragment 的引用
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public BasePagerAdapter(FragmentManager fm, PagerFragCreator<T, F> creator) {
        super(fm);
        this.mCreator = creator;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    /**
     * 这里要用于创建新的Fragment，而不是获取
     * @param i
     * @return
     */
    @Override
    public Fragment getItem(int i) {
        return mCreator.createFragment(mData.get(i), i);
    }

    /**
     * 自定义获取缓存中的fragment
     */
    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 此函数会调用getItem()函数，返回新的函数，新的对象将会被FragmentTransaction.add().FragmentStatePagerAdapter就是通过这种方式
     * 每一次都会创建新的Fragment，而在不需要的情况下立刻释放资源，来达到节省内存的目的
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    /**
     * 将Fragment移除，此时使用的FragementTransaction.remove(),并释放其资源：
     * 这里我们加上一句，移除registeredFragments缓存的fragment
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mCreator.createTitle(mData.get(position));
    }

    public interface PagerFragCreator<T, F> {

        F createFragment(T data, int position);

        String createTitle(T data);
    }
}
