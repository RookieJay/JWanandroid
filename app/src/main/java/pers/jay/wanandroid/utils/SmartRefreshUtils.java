package pers.jay.wanandroid.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.common.Const;
import pers.zjc.commonlibs.util.StringUtils;
import timber.log.Timber;

/**
 * 刷新的辅助类
 *
 * @author Cuizhen
 * @date 2018/7/6-下午5:06
 */
public class SmartRefreshUtils {

    private static final int FIRST_PAGE = 0;

    private final RefreshLayout mRefreshLayout;
    private RefreshListener mRefreshListener = null;
    private LoadMoreListener mLoadMoreListener = null;

    private int currentPage = FIRST_PAGE;
    private int perPageCount = 0;

    private boolean showPoem;

    public static SmartRefreshUtils with(RefreshLayout layout) {
        return new SmartRefreshUtils(layout);
    }

    private SmartRefreshUtils(RefreshLayout layout) {
        mRefreshLayout = layout;
        mRefreshLayout.setEnableAutoLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(true);
        // 每次初始化恢复
        showPoem = false;
    }

    public SmartRefreshUtils pureScrollMode() {
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnablePureScrollMode(true);
        mRefreshLayout.setEnableNestedScroll(true);
        mRefreshLayout.setEnableOverScrollDrag(true);
        return this;
    }

    public SmartRefreshUtils setRefreshHeader(@NonNull RefreshHeader header) {
        mRefreshLayout.setRefreshHeader(header);
        return this;
    }

    public SmartRefreshUtils setRefreshListener(@Nullable RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
        if (refreshListener == null) {
            mRefreshLayout.setEnableRefresh(false);
        }
        else {
            mRefreshLayout.setEnablePureScrollMode(false);
            mRefreshLayout.setEnableRefresh(true);
            mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishRefresh((int)Const.HttpConst.DEFAULT_TIMEOUT, false, false);
                    if (showPoem) {
//                        PoemUtils.getPoemAsync(mRefreshLayout);
                    }
                    mRefreshListener.onRefresh();
                }
            });
        }
        return this;
    }

    public SmartRefreshUtils setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
        if (loadMoreListener == null) {
            mRefreshLayout.setEnableLoadMore(false);
        }
        else {
            mRefreshLayout.setEnablePureScrollMode(false);
            mRefreshLayout.setEnableLoadMore(true);
            mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishLoadMore((int)Const.HttpConst.DEFAULT_TIMEOUT);
                    mLoadMoreListener.onLoadMore();
                }
            });
        }
        return this;
    }

    public void autoRefresh() {
        mRefreshLayout.autoRefresh();
    }

    public void autoLoadMore() {
        mRefreshLayout.autoLoadMore();
    }

    public void success() {
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.finishLoadMore(true);
    }

    public void fail() {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    public SmartRefreshUtils showPoem() {
        this.showPoem = true;
        RefreshHeader header = mRefreshLayout.getRefreshHeader();
        if (header == null) {
            throw new IllegalStateException("the RefreshHeader can not be null");
        }
        String poem = AppConfig.getInstance().getPoem();
        Timber.e(StringUtils.isEmpty(poem) ?  "空的呢" : poem);
        if (header instanceof StoreHouseHeader) {
            ((StoreHouseHeader)header).initWithString("WANANDROID");
        } else if (header instanceof ClassicsHeader) {
            ((ClassicsHeader)header).setLastUpdateText(poem);
        }
        mRefreshLayout.setRefreshHeader(header);
        return this;
    }

    public boolean isFinishing() {
        return mRefreshLayout.getState().isFinishing;
    }

    public interface RefreshListener {

        void onRefresh();
    }

    public interface LoadMoreListener {

        void onLoadMore();
    }

}
