package pers.jay.wanandroid.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import pers.jay.wanandroid.R;

/**
 * 自定义控件流程
 * 1.新建自定义控件类，实现构造方法。
 * 构造函数参数说明：
 * ·Context - 上下文；
 * ·AttributeSet - xml文件中的属性；
 * ·int defStyleAttr - Theme中的默认样式；
 * ·int defStyleResource - defStyleAttr未使用（为0，或者未匹配到），则应用于View的默认样式；
 * ·R.style中系统为view定义了很多默认主题Theme,主题中有对某些属性的默认赋值。
 * 调用四种构造方法的时机：
 * 1.1 方法一：不需要在xml文件中使用(直接new出来的)控件;
 * 1.2 方法二：需要在xml中定义，需要传入属性值的时候;
 * 1.3 方法三：系统不会自动去调用，而是用户自己需要的时候去主动调用。
 * 1.4 方法四：系统不会自动去调用，而是用户自己需要的时候去主动调用。
 * 2.自定义属性(如果需要)：在values目录下新建attrs.xml文件，自定义属性，declare-styleable的name要跟自定义控件的类名相同；
 * 3.在构造方法中解析自定义属性，通过context.obtainStyledAttributes获取TypedArray，
 * 4.初始化画笔
 * 5.重写onMeasure
 * 6.重写onDraw方法（如果是ViewGroup还要重写onLayout方法）
 */
public class DashboardView extends View {

    private Context mContext;

    // 圆心坐标
    private int mCenterX, mCenterY;
    // 最大进度
    private int maxProgress = 100;
    // 当前进度
    private float progress;
    // 背景颜色
    private int mBgColor = Color.GRAY;
    // 弧线颜色
    private int mArcColor = Color.WHITE;
    // 弧线宽度
    private float mArcWidth;
    // 中心文字颜色
    private int mCenterTextColor = Color.BLACK;
    // 中心文字大小
    private float mCenterTextSize;
    // 圆半径
    private int mCircleRadius = 100;
    // 起始角度
    private float startAngle = 0;
    // 扫过最大角度
    private float maxAngle = 360;
    // 圆弧与控件的间隔
    private float arcPadding = 0;
    // 绘制区域
    private RectF arcRectF;
    // 背景弧线画笔
    private Paint bgArcPaint;
    // 进度弧线画笔
    private Paint progressPaint;
    // 中心文字画笔
    private Paint textPaint;

    public DashboardView(Context context) {
        this(context, null);
    }

    public DashboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initArgs(context, attrs);
        initPaint();
    }

    private void initArgs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DashboardView);
        maxProgress = array.getInteger(R.styleable.DashboardView_dv_max, maxProgress);
        progress = array.getFloat(R.styleable.DashboardView_dv_progress, progress);
        mBgColor = array.getColor(R.styleable.DashboardView_dv_bgColor, mBgColor);
        mArcColor = array.getColor(R.styleable.DashboardView_dv_arcColor, mArcColor);
        mArcWidth = array.getDimension(R.styleable.DashboardView_dv_arcWidth, dp2px(5));
        mCenterTextColor = array.getColor(R.styleable.DashboardView_dv_centerTextColor,
                mCenterTextColor);
        mCenterTextSize = array.getDimension(R.styleable.DashboardView_dv_centerTextSize,
                sp2px(14));
        mCircleRadius = array.getInteger(R.styleable.DashboardView_dv_circleRadius, mCircleRadius);
        startAngle = array.getFloat(R.styleable.DashboardView_dv_startAngle, startAngle);
        maxAngle = array.getFloat(R.styleable.DashboardView_dv_maxAngle, maxAngle);
        arcPadding = array.getDimension(R.styleable.DashboardView_dv_arcPadding, dp2px(mArcWidth));
        array.recycle();
    }

    public void initPaint() {
        // 画笔 背景圆弧
        bgArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgArcPaint.setStyle(Paint.Style.STROKE);
        bgArcPaint.setStrokeWidth(mArcWidth);
        bgArcPaint.setColor(mBgColor); //getResources().getColor(R.color.deep_gray, null)
        bgArcPaint.setStrokeCap(Paint.Cap.ROUND);

        // 画笔 进度圆弧
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(mArcColor);
        progressPaint.setStrokeWidth(mArcWidth);
        // 设置画笔的线冒样式,即端点样式 Paint.Cap.BUTT：无 Paint.Cap.SQUARE：方形 Paint.Cap.ROUND： 半圆形0
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // 画笔 文字
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(mCenterTextSize);
        textPaint.setColor(mCenterTextColor);
//        textPaint.setTextAlign(Paint.Align.CENTER);

        // 矩形区域
        arcRectF = new RectF();
    }

    /**
     * 比onDraw先执行
     * <p>
     * 一个MeasureSpec封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。
     * 一个MeasureSpec由大小和模式组成
     * 它有三种模式：UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小;
     * EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
     * AT_MOST(至多)，子元素至多达到指定大小的值。
     * <p>
     * 它常用的三个函数：
     * 1.static int getMode(int measureSpec):根据提供的测量值(格式)提取模式(上述三个模式之一)
     * 2.static int getSize(int measureSpec):根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
     * 3.static int makeMeasureSpec(int size,int mode):根据提供的大小值和模式创建一个测量值(格式)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 先执行原测量算法
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取原来的测量结果
//        getMeasuredWidth();getMeasuredHeight();
        // 利用原来的测量结果计算新的尺寸
        int width = measureView(widthMeasureSpec);
        int height = measureView(heightMeasureSpec);
        // 保存计算后的结果
        setMeasuredDimension(width, height);
    }

    /**
     * 测量就是为了告诉系统我这个控件该画多大，如果是给了确定的值比如设置的是Match_parent或者特定的dp值就很简单了，
     * 即按照measureSpec给出的大小返回就行，如果设置的是wrap_content，系统本身是不知道你的控件内部元素到底有多大的，
     * 所以就需要计算出一个最小值告诉给系统
     *
     * 如上述代码所示，如果判断得到设置的模式是MeasureSpec.EXACTLY，就把MeasureSpec中的尺寸值返回就行，
     * 如果判断得到设置的模式是MeasureSpec.AT_MOST，也就是代码中设置的 wrap_content，就比较圆环的直径和MeasureSpec中给出的尺寸值，
     * 取最小的一个返回，最后调用setMeasuredDimension方法，传入处理后的长宽值。
     *
     * specMode一共有三种类型，如下所示：
     * 1. EXACTLY
     * 表示父视图希望子视图的大小应该是由specSize的值来决定的，系统默认会按照这个规则来设置子视图的大小，简单的说（当设置width或height为match_parent时，模式为EXACTLY，因为子view会占据剩余容器的空间，所以它大小是确定的）
     * 2. AT_MOST
     * 表示子视图最多只能是specSize中指定的大小。（当设置为wrap_content时，模式为AT_MOST, 表示子view的大小最多是多少，这样子view会根据这个上限来设置自己的尺寸）
     * 3. UNSPECIFIED
     * 表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制。这种情况比较少见，不太会用到。
     */
    private int measureView(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            // MATCH_PARENT
            result = specSize;
        }
        else {
            result = mCircleRadius * 2;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 设置圆心坐标
        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        // 设置矩形大小 因为画笔有一定的宽度，所以画圆弧的范围要比View本身的大小稍微小一些，往中间缩mArcWidth的长度，不然画笔画出来的东西会显示不完整
        arcRectF.left = mCenterX - mCircleRadius + mArcWidth + arcPadding;
        arcRectF.top = mCenterY - mCircleRadius + mArcWidth + arcPadding;
        arcRectF.right = mCircleRadius + mCenterX - mArcWidth - arcPadding;
        arcRectF.bottom = mCircleRadius + mCenterY - mArcWidth - arcPadding;
        // 绘制测试矩形大小
//        canvas.drawRect(arcRectF, bgArcPaint);
        // 第一步：绘制背景灰色圆弧,
        canvas.drawArc(arcRectF, startAngle, maxAngle, false, bgArcPaint);
        //        // 第二步：绘制当前进度的圆弧
        //        // 计算并控制当前进度 列式：当前进度/最大进度 = 扫过角度/扫过最大角度
        float sweepAngle = maxAngle * progress / maxProgress;
        canvas.drawArc(arcRectF, startAngle, sweepAngle, false, progressPaint);
        // 第三步，绘制文字(参数为：文本，基线x，基线y, 画笔)，基线是文字左下角那个点的位置,横坐标等于View宽度一半-文字宽度一半
        // 在绘制内容会产生变化的情况下，比如动态地把“bbbb”改成了“aaaa”或者其他文字，我们就用FontMetircs测量；
        // 绘制内容不发生如何变化的，就直接用getTextBounds（）就可以了
        // 目的：把文字画在 View 的中心点位置
        String text = String.valueOf(Math.round(progress));
        float textWidth = textPaint.measureText(text, 0, text.length());
        float dx = getWidth() / 2 - textWidth / 2;
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        float dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        float baseLine = getHeight() / 2 + dy;
        canvas.drawText(text, dx, baseLine, textPaint);

        // 辅助十字线，确定圆心位置
//        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), progressPaint);
//        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, progressPaint);
    }

    /**
     * 设置进度及动画，重新绘制
     *
     * @param max    最大值
     * @param target 目标值
     */
    public void setProgress(int max, int target) {
        this.maxProgress = max;
        playAnimator(target);
    }

    /**
     * ValuAnimator本质上就是通过设置一个起始值和结束值，来取到一个从起始值到结束值的一个逐渐增长的Animation值。
     * 在draw方法中使用这个值并且不断的重绘，就能达到一种动画效果。
     * TimeInterpolator 插值器 能够使Animation值的变化产生加速增长、减速增长、先加速后减速、回弹等效果。可继承BaseInterpolator自定义插值器
     */
    private void playAnimator(int target) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, target);
        animator.setDuration(3000L);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float current = (float)animation.getAnimatedValue();
                // 此处直接赋值会导致中间文字更新过程中位置不对的问题
                progress = Math.round(current);
                invalidate();
            }
        });
        animator.start();
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
