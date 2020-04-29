package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author RookieJay
 * 三角形标签
 */
public class TriangleTextView extends View {

    public TriangleTextView(Context context) {
        super(context);
    }

    public TriangleTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TriangleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TriangleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        Path path = new Path();

    }
}
