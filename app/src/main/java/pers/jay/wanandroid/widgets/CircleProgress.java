package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import pers.jay.wanandroid.R;

public class CircleProgress extends View {

    private static final int defaultCircleStrokeWidth = 4;
    private static final int progressWidth = 4;
    private static final int defaultCircleRadius = 45;

    private int mSolidColor;
    private int mStrokeColor;
    private int mProgressColor;
    private float mSolidWidth;
    private float mProgressWidth;
    private float mCircleRadius;
    private Paint defaultCirclePaint;
    private Paint progressPaint;
    private Paint smallCirclePaint;
    private Paint smallCircleSolidPaint;

    private float currentAngle;
    private float mStartSweepValue = 270f;

    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    public CircleProgress(Context context) {
        super(context);
        init();
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 获取自定义属性
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mSolidColor = typedArray.getColor(R.styleable.CircleProgress_cp_solid_color, getResources().getColor(R.color.base_bg_color));
        mProgressColor = typedArray.getColor(R.styleable.CircleProgress_cp_progress_color, getResources().getColor(R.color.red));
        mStrokeColor = typedArray.getColor(R.styleable.CircleProgress_cp_stroke_color, getResources().getColor(R.color.gray));
        mSolidWidth = typedArray.getDimension(R.styleable.CircleProgress_cp_stroke_width, dp2px(2));
        mProgressWidth = typedArray.getDimension(R.styleable.CircleProgress_cp_stroke_width, dp2px(2));
        mCircleRadius = typedArray.getDimension(R.styleable.CircleProgress_cp_stroke_width, dp2px(2));
        //回收typedArray对象
        typedArray.recycle();
        //设置画笔
        init();
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        //默认圆
        defaultCirclePaint = new Paint();
        defaultCirclePaint.setAntiAlias(true);//抗锯齿
        defaultCirclePaint.setDither(true);//防抖动
        defaultCirclePaint.setStyle(Paint.Style.STROKE);
        defaultCirclePaint.setStrokeWidth(3f);
        defaultCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.gray));//这里先画边框的颜色，后续再添加画笔画实心的颜色
        //默认圆上面的进度弧度
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(4f);
        progressPaint.setColor(ContextCompat.getColor(getContext(), R.color.gray));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔笔刷样式
//        //进度上面的小圆
//        smallCirclePaint = new Paint();
//        smallCirclePaint.setAntiAlias(true);
//        smallCirclePaint.setDither(true);
//        smallCirclePaint.setStyle(Paint.Style.STROKE);
//        smallCirclePaint.setStrokeWidth(4f);
//        smallCirclePaint.setColor(getResources().getColor(R.color.red));
//        //画进度上面的小圆的实心画笔（主要是将小圆的实心颜色设置成白色）
//        smallCircleSolidPaint = new Paint();
//        smallCircleSolidPaint.setAntiAlias(true);
//        smallCircleSolidPaint.setDither(true);
//        smallCircleSolidPaint.setStyle(Paint.Style.FILL);
//        smallCircleSolidPaint.setColor(getResources().getColor(R.color.red));

        //文字画笔
//        textPaint = new Paint();
//        textPaint.setAntiAlias(true);
//        textPaint.setDither(true);
//        textPaint.setStyle(Paint.Style.FILL);
//        textPaint.setColor(textColor);
//        textPaint.setTextSize(textSize);
    }

    /**
     * 如果该View布局的宽高开发者没有精确的告诉，则需要进行测量，如果给出了精确的宽高则我们就不管了
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width;
        int height;
        int strokeWidth = Math.max(defaultCircleStrokeWidth, progressWidth);
        if(widthMode != MeasureSpec.EXACTLY){
            width = getPaddingLeft() + defaultCircleRadius * 2 + strokeWidth + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }
        if(heightMode != MeasureSpec.EXACTLY){
            height = getPaddingTop() + defaultCircleRadius*2 + strokeWidth + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        //  getPaddingLeft获取传入的padding值
        canvas.translate(getPaddingLeft(), getPaddingTop());
        // 画默认大圆
        canvas.drawCircle(defaultCircleRadius, defaultCircleRadius, defaultCircleRadius, defaultCirclePaint);
        // 画进度圆弧
//        currentAngle = getProgress()* 1.0f / getMax()*360;
        canvas.drawArc(new RectF(0, 0, defaultCircleRadius*2, defaultCircleRadius*2), mStartSweepValue, currentAngle ,false, progressPaint);
        canvas.restore();
    }

    protected int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
