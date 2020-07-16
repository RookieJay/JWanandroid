package pers.jay.wanandroid.mvp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import pers.jay.wanandroid.base.BaseLazyLoadFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import per.goweii.anylayer.Align;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.Layer;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.ScrollTopListener;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.activity.TodoEditActivity;
import pers.jay.wanandroid.mvp.ui.adapter.TodoTypeAdapter;
import pers.jay.wanandroid.utils.WrapContentLinearLayoutManager;
import pers.jay.wanandroid.widgets.DividerItemDecoration;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.ScreenUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class TodoTabFragment extends BaseLazyLoadFragment {

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
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fabTop)
    FloatingActionButton fabTop;

    private RecyclerView popRv;

    private List<String> mTitles = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private TodoTypeAdapter mTodoTypeAdapter;
    private List<String> mTodoCats = new ArrayList<>();
    /*待办已办类型*/
    private int mTodoType;
    /*分组如工作、学习*/
    private int mTodoCat = 1;
    private int mPosition;
    private MainActivity mActivity;
    private Layer popWindow;
    private View popView;

    public static Fragment newInstance() {
        return new TodoTabFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (MainActivity)context;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tab, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initTodoType();
        initFragments();
        setView();
        initPopView();
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    /**
     * todo类型，先暂定有限种
     */
    private void initTodoType() {
        mTodoCats.add("工作");
        mTodoCats.add("生活");
        mTodoCats.add("娱乐");
    }

    private void initFragments() {
        mTitles.add("待办");
        mTitles.add("已办");
        Bundle todoArgs = new Bundle();
        todoArgs.putInt(Const.Key.KEY_FRAGMENT, TodoFragment.TYPE_TODO);
        TodoFragment todoFragment = TodoFragment.newInstance();
        todoFragment.setArguments(todoArgs);
        mFragments.add(todoFragment);
        DoneFragment doneFragment = DoneFragment.newInstance();
        Bundle doneArgs = new Bundle();
        doneArgs.putInt(Const.Key.KEY_FRAGMENT, TodoFragment.TYPE_DONE);
        doneFragment.setArguments(doneArgs);
        mFragments.add(doneFragment);
    }

    private void setView() {
        initFab();
        initToolbar();
        initTabLayout();
    }

    private void initFab() {
        fabTop.setImageDrawable(mContext.getDrawable(R.drawable.vector_drawable_ic_edit));
        fabTop.setOnClickListener(v -> openTodoEditPage());
    }

    private void initPopView() {
        popView = LayoutInflater.from(mContext).inflate(R.layout.layout_popup_list, null, false);
        popWindow = AnyLayer.popup(ivRight)
                            .align(Align.Direction.VERTICAL, Align.Horizontal.CENTER, Align.Vertical.BELOW, false)
                            .contentView(popView)
                            .cancelableOnTouchOutside(true)
                            .bindData(layer -> {
                                popRv = layer.getView(R.id.mRecyclerView);
                                popRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL_LIST));
                                ArmsUtils.configRecyclerView(popRv, new WrapContentLinearLayoutManager(mContext));
                                mTodoTypeAdapter = new TodoTypeAdapter(R.layout.popup_todo_item_list, mTodoCats);
                                mTodoTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
                                    hidePopMenu();
                                    mTodoType = viewPager.getCurrentItem();
                                    mPosition = position;
                                    mTodoCat = position + 1;
                                    tvTitle.setText(String.format(getResources().getString(R.string.navi_todo) + "(%s)",
                                            mTodoCats.get(position)));
                                    Fragment fragment = mFragments.get(mTodoType);
                                    if (fragment instanceof TodoFragment && fragment.isAdded() && fragment.isVisible() && fragment
                                            .getUserVisibleHint()) {
                                        ((TodoFragment)fragment).changeType(mTodoCat);
                                    }
                                });
                                popRv.setAdapter(mTodoTypeAdapter);
                            });
    }

    private void openTodoEditPage() {
        Intent intent = new Intent(mContext, TodoEditActivity.class);
        intent.putExtra(Const.Key.KEY_TODO_TYPE, mTodoType);
        intent.putExtra(Const.Key.KEY_TODO_CAT, mTodoCat);
        intent.putExtra(Const.Key.KEY_TITLE, mTodoCats.get(mPosition));
        intent.putExtra(Const.Key.KEY_START_TYPE, TodoEditActivity.TYPE_ADD);
        launchActivity(intent);
    }

    private void onScrollTop() {
        List<Fragment> fragments = mActivity.getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ScrollTopListener && fragment.isAdded()) {
                ((ScrollTopListener)fragment).scrollToTop();
            }
        }
    }

    private void initToolbar() {
        tvTitle.setText(getResources().getString(R.string.navi_todo));
        ivLeft.setOnClickListener(v -> killMyself());
        ivRight.setImageDrawable(mContext.getDrawable(R.drawable.ic_swap_horiz_black_24dp));
        ivRight.setOnClickListener(v -> showPopMenu());
    }

    private void showPopMenu() {
        if (popWindow != null && !popWindow.isShow()) {
            popWindow.show();
            if (popView == null) {
                return;
            }
            ViewGroup.LayoutParams params = popView.getLayoutParams();
            params.width = ScreenUtils.getScreenWidth() / 2;
            params.height = ScreenUtils.getScreenWidth() / 3;
            popView.setLayoutParams(params);
        }
    }

    private void hidePopMenu() {
        if (popWindow != null && popWindow.isShow()) {
            popWindow.dismiss();
        }
    }

    private void initTabLayout() {
        tabLayout.setTabSpaceEqual(true);
        viewPager.setAdapter(new InnerPagerAdapter(getChildFragmentManager(), mFragments,
                mTitles.toArray(new String[0])));
        tabLayout.setViewPager(viewPager);
        tabLayout.setCurrentTab(0, true);
    }

    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
    }

    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    public void killMyself() {
        FragmentUtils.pop(mActivity.getSupportFragmentManager(), true);
    }

    @Override
    protected void lazyLoadData() {

    }

    static class InnerPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments = new ArrayList<>();
        private String[] titles;

        public InnerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments,
                                 String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
            // super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    public boolean onBackPress() {
        killMyself();
        return true;
    }

}
