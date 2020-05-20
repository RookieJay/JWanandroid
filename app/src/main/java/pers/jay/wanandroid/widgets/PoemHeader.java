package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ArrowDrawable;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.util.SmartUtil;

import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.utils.PoemUtils;
import pers.zjc.commonlibs.util.StringUtils;

/**
 * 自定义刷新header，显示今日诗词
 */
public class PoemHeader extends LinearLayout implements RefreshHeader {

    private static String poem;

    private TextView mHeaderText;//标题文本
    private ImageView mArrowView;//下拉箭头
    private ImageView mProgressView;//刷新动画视图
    private ProgressDrawable mProgressDrawable;//刷新动画
    private RefreshKernel mRefreshKernel;
    private int mBackgroundColor = 0;
    private int mDrawableColor = 0x2F2D2E;

    public PoemHeader(Context context) {
        this(context, null);
    }

    public PoemHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        poem = AppConfig.getInstance().getPoem();
        setGravity(Gravity.CENTER);
        mHeaderText = new TextView(context);
        mHeaderText.setTextSize(12f);
        int textColor = mHeaderText.getCurrentTextColor();
        mProgressDrawable = new ProgressDrawable();
        mProgressDrawable.setColor(textColor);
        mArrowView = new ImageView(context);
        mProgressView = new ImageView(context);
        mProgressView.setImageDrawable(mProgressDrawable);
        mArrowView.setImageDrawable(new ArrowDrawable());
        mArrowView.setColorFilter(textColor);
        addView(mProgressView, SmartUtil.dp2px(16), SmartUtil.dp2px(16));
        addView(mArrowView, SmartUtil.dp2px(16), SmartUtil.dp2px(16));
        addView(new Space(context), SmartUtil.dp2px(16), SmartUtil.dp2px(16));
        addView(mHeaderText, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setMinimumHeight(SmartUtil.dp2px(60));
    }

    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        mProgressDrawable.start();//开始动画
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        mProgressDrawable.stop();//停止动画
        mProgressView.setVisibility(GONE);//隐藏动画
        if (success) {
            mHeaderText.setText(StringUtils.isEmpty(poem) ? "刷新完成" : poem);
        }
        else {
            mHeaderText.setText(StringUtils.isEmpty(poem) ? "刷新失败" : poem);
        }
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState,
                               @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                mHeaderText.setText(StringUtils.isEmpty(poem) ? "下拉开始刷新" : poem);
                mArrowView.setVisibility(VISIBLE);//显示下拉箭头
                mProgressView.setVisibility(GONE);//隐藏动画
                mArrowView.animate().rotation(0);//还原箭头方向
                break;
            case Refreshing:
                mHeaderText.setText(StringUtils.isEmpty(poem) ? "正在刷新" : poem);
                mProgressView.setVisibility(VISIBLE);//显示加载动画
                mArrowView.setVisibility(GONE);//隐藏箭头
                break;
            case ReleaseToRefresh:
                mHeaderText.setText(StringUtils.isEmpty(poem) ? "释放立即刷新" : poem);
                mArrowView.animate().rotation(180);//显示箭头改为朝上
                break;
        }
    }

    @Override
    public void setPrimaryColors(int... colors) {
        if (colors.length > 0) {
            mBackgroundColor = colors[0];
            if (mRefreshKernel != null) {
                mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
            }
            if (colors.length > 1) {
                mHeaderText.setTextColor(colors[1]);
                mProgressDrawable.setColor(colors[1]);
                mArrowView.setColorFilter(colors[1]);
            }
        }
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        //        kernel.requestDrawBackgroundForHeader(mBackgroundColor);
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgroundFor(this, mBackgroundColor);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height,
                         int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        PoemUtils.getPoemAsync(() -> {
            String poem = AppConfig.getInstance().getPoem();
            setHeaderText(poem);
        });
    }

    /**
     * 设置header文本，延迟刷新
     */
    public void setHeaderText(String text) {
        postDelayed(() -> {
            poem = text;
            postInvalidate();
        }, 2000L);
    }

}
