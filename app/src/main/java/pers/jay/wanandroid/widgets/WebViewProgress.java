package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import pers.jay.wanandroid.R;

/**
 * 平滑过渡的webView进度条
 */
public class WebViewProgress extends View {

    private static final int DEFAULT_MAX_PROGRESS = 0;
    private static final int DEFAULT_MAX_VALUE = 100;

    private Paint progressPaint;

    // 最大值
    private int max;
    // 进度
    private int progress;
    // 进度条颜色
    private int color;
    private Point leftTop;
    private Point rightBottom;
    private int mWidth;
    private int mHeight;
    // 是否已经加载动画
    private boolean mAnimated;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (max == 0) {
            throw new IllegalArgumentException("The max value must be greater than 0.");
        }
        this.progress = progress;
        // 重绘
        invalidate();
        if (progress == max && !mAnimated) {
            mAnimated = true;
            // 这里可能产生多次传入最大值，采用mAnimated变量控制
            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
            animation.setDuration(500L);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    hide();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            startAnimation(animation);
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public WebViewProgress(Context context) {
        this(context, null);
    }

    public WebViewProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray tp = context.obtainStyledAttributes(attrs, R.styleable.WebViewProgress);
        max = tp.getInteger(R.styleable.WebViewProgress_max, DEFAULT_MAX_VALUE);
        progress = tp.getInteger(R.styleable.WebViewProgress_progress, DEFAULT_MAX_PROGRESS);
        color = tp.getColor(R.styleable.WebViewProgress_progress_color, getResources().getColor(R.color.colorPrimaryDark, null));
        tp.recycle();
        init();
    }

    private void init() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);//抗锯齿
        progressPaint.setDither(true);//防抖动
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setStrokeWidth(3f);
        //添加画笔画实心的颜色
        progressPaint.setColor(color);
        leftTop = new Point(0, 0);
        rightBottom = new Point(getWidth(), getHeight());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, ow, oh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth * progress / 100, mHeight, progressPaint);
        super.onDraw(canvas);
    }

    public void hide() {
        setVisibility(GONE);
    }

}
