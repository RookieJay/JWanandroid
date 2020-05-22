package pers.jay.wanandroid.mvp.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.SearchHelper;
import pers.jay.wanandroid.mvp.ui.fragment.SearchHisFragment;
import pers.jay.wanandroid.mvp.ui.fragment.SearchResultFragment;
import pers.jay.wanandroid.utils.DrawableUtil;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.FragmentUtils;
import pers.zjc.commonlibs.util.KeyboardUtils;
import pers.zjc.commonlibs.util.StringUtils;

/**
 * @author ZJC
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.flSearch)
    FrameLayout flSearch;

    private Drawable mdrClear;
    private String mSearchKey = "";
    private SearchHisFragment searchFragment = SearchHisFragment.newInstance();
    private SearchResultFragment searchResultFragment = SearchResultFragment.newInstance();
    private OnSearchListener onSearchListener;

    public void setOnSearchListener(OnSearchListener onSearchListener) {
        this.onSearchListener = onSearchListener;
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        UIUtils.setSameColorBar(true, getWindow(), getResources());
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean useFragment() {
        return true;
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_search;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        setUpView();
        initFragment();
    }

    private void initFragment() {
        FragmentUtils.add(getSupportFragmentManager(), searchFragment, R.id.flSearch);
        FragmentUtils.add(getSupportFragmentManager(), searchResultFragment, R.id.flSearch);
        showSearchFragment();
    }

    private void setUpView() {
        UIUtils.showSoftInputFromWindow(this, etSearch);
        mdrClear = ContextCompat.getDrawable(this, R.drawable.ic_clear);
        if (mdrClear != null) {
            mdrClear.setBounds(0, 0, 45, 45);
        }
        ivLeft.setOnClickListener(v -> killMyself());
        ivRight.setOnClickListener(v -> doSearch());
        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
            }
            return false;
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtils.isEmpty(s)) {
                    etSearch.setCompoundDrawables(null, null, null, null);
                } else {
                    etSearch.setCompoundDrawables(null, null, mdrClear, null);
                }
                mSearchKey = etSearch.getText().toString();
            }
        });
        DrawableUtil drawableUtil = new DrawableUtil(etSearch);
        drawableUtil.setListener(new DrawableUtil.OnDrawableListener() {

            @Override
            public void onLeft(View v, Drawable left) {

            }

            @Override
            public void onRight(View v, Drawable right) {
                etSearch.getText().clear();
                etSearch.setCompoundDrawables(null, null, null, null);
            }
        });
    }

    private void doSearch() {
        doSearch(mSearchKey);
    }

    private void hideKeyboard() {
        KeyboardUtils.hideSoftInput(etSearch);
    }

    public void doSearch(String key) {
        hideKeyboard();
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (StringUtils.isEmpty(mSearchKey) || !StringUtils.equals(mSearchKey, key)) {
            etSearch.setText(key);
        }
        mSearchKey = key;
        SearchHelper.getInstance().addSearchKey(mSearchKey);
        onSearchListener.onSearch(mSearchKey);
        searchFragment.addHistory(mSearchKey);
    }

    private void killMyself() {
        if (searchResultFragment.isAdded() && searchResultFragment.isVisible()) {
            showSearchFragment();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        killMyself();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchFragment.onDetach();
        searchFragment = null;
        etSearch = null;
        mdrClear = null;
    }

    public void showResultFragment() {
        FragmentUtils.showHide(searchResultFragment, searchFragment);
    }

    public void showSearchFragment() {
        FragmentUtils.showHide(searchFragment, searchResultFragment);
    }

    public interface OnSearchListener {

        /**
         * 搜索回调
         * @param key 搜索关键词
         *
         */
        void onSearch(String key);
    }
}
