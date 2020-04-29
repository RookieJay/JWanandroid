package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import pers.jay.wanandroid.R;

public class KeyboardLayout extends RelativeLayout {

    private Rect mWindowVisibleDisplayFrame;
    private int mAboveKeyboardViewID = NO_ID;
    private Drawable mShowKeyboardBackground;
    private Drawable mHideKeyboardBackground;

    public KeyboardLayout(Context context) {
        super(context);
        init(null);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public void reset() {
        scrollTo(0, 0);
        if (null != mHideKeyboardBackground) {
            setBackground(mHideKeyboardBackground);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAboveKeyboardViewID != NO_ID) {
            View v = findViewById(mAboveKeyboardViewID);
            v.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    getWindowVisibleDisplayFrame(mWindowVisibleDisplayFrame);
                    int moveY = mWindowVisibleDisplayFrame.bottom - bottom - mWindowVisibleDisplayFrame.top;
                    if (moveY <= 0) {
                        scrollTo(0, Math.abs(moveY));
                        if (null != mShowKeyboardBackground) {
                            setBackground(mShowKeyboardBackground);
                        }
                    }
                    else {
                        reset();
                    }
                }
            });
        }
        reset();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
            scrollTo(0, 0);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init(AttributeSet attrs) {
        mWindowVisibleDisplayFrame = new Rect();
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.KeyboardLayout);
        mAboveKeyboardViewID = a.getResourceId(R.styleable.KeyboardLayout_above_keyboard_view, NO_ID);
        mShowKeyboardBackground = a.getDrawable(R.styleable.KeyboardLayout_show_keyboard_background);
        mHideKeyboardBackground = a.getDrawable(R.styleable.KeyboardLayout_hide_keyboard_background);
        a.recycle();
    }
}
