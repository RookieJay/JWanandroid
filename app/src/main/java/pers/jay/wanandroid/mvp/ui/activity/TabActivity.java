package pers.jay.wanandroid.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.mvp.presenter.TabPresenter;
import pers.jay.wanandroid.mvp.ui.fragment.TabFragment;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.ToastUtils;

public class TabActivity extends BaseActivity<TabPresenter> {

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

    private int curPosition;

    private List<Article> mArticles = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private String mTitle;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_tab; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent == null) {
            ToastUtils.showShort("intent data is null");
            killMyself();
            return;
        }
        int type = intent.getIntExtra(Const.Key.KEY_TAB_FROM_TYPE, 0);
        if (type == 0) {
            ToastUtils.showShort("非法类型参数");
            return;
        }
        switch (type) {
            case Const.Type.TYPE_TAB_KNOWLEDGE:
                loadKnowledgeData(intent);
                break;
            case Const.Type.TYPE_TAB_WEIXIN:
                break;
            default:
                break;
        }
        setView();
    }

    private void loadKnowledgeData(Intent intent) {
        Tab data = intent.getParcelableExtra(Const.Key.KEY_TAB_DATA);
        curPosition = intent.getIntExtra(Const.Key.KEY_TAB_CHILD_POSITION, -1);
        if (data == null) {
            showMessage("数据项为空");
            return;
        }
        mTitle = data.getName();
        for (Tab tab : data.getChildren()) {
            mTitles.add(tab.getName());
            TabFragment fragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Const.Key.KEY_TAB_DATA, tab);
            bundle.putInt(Const.Key.KEY_TAB_CHILD_POSITION, curPosition);
            bundle.putInt(Const.Key.KEY_TAB_FROM_TYPE, Const.Type.TYPE_TAB_KNOWLEDGE);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
    }

    private void setView() {
        initToolbar();
        initViewPager();
        initTabLayout();
    }

    private void initToolbar() {
        tvTitle.setText(StringUtils.isEmpty(mTitle) ? "知识体系" : mTitle);
        ivLeft.setOnClickListener(v -> killMyself());
    }

    private void initTabLayout() {
        if (curPosition == -1) {
            showMessage("非法参数");
            return;
        }
        tabLayout.setViewPager(viewPager, mTitles.toArray(new String[0]), this,
                mFragments);
        tabLayout.setCurrentTab(curPosition, true);
    }

    private void initViewPager() {
        viewPager.setOffscreenPageLimit(mFragments.size() / 2);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragments.get(i);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        });
    }

    public void killMyself() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIUtils.setSameColorBar(true, getWindow(), getResources());
        super.onCreate(savedInstanceState);
    }

    public void showMessage(String msg) {
        ToastUtils.showShort(msg);
    }

}
