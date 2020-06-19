package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import pers.jay.wanandroid.common.JApplication;

/**
 * 方正沈尹默行书 简繁
 */
public class HandWritingTextView extends AppCompatTextView {

    public HandWritingTextView(Context context) {
        this(context, null);
    }

    public HandWritingTextView(Context context,
                               @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandWritingTextView(Context context,
                               @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        Typeface tf = genTypeface();
//        setTypeface(tf);
    }

    /**
     * 获取自定义字体
     * @return
     */
    private Typeface genTypeface() {
        return Typeface.createFromAsset(JApplication.getInstance().getAssets(), "fonts/FZShenYMXSJF.TTF");
    }
}
