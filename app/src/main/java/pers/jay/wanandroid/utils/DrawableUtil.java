package pers.jay.wanandroid.utils;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class DrawableUtil {

    /**
     * TextView四周drawable的序号。
     * 0 left,  1 top, 2 right, 3 bottom
     */
    private final int LEFT = 0;
    private final int RIGHT = 2;

    private OnDrawableListener listener;
    private EditText mTextView;

    public DrawableUtil(EditText view) {
        mTextView = view;
    }

    public interface OnDrawableListener {
        void onLeft(View v, Drawable left);

        void onRight(View v, Drawable right);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setListener(OnDrawableListener listener) {
        this.listener = listener;
        mTextView.setOnTouchListener(mOnTouchListener);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (listener != null) {
                        Drawable drawableLeft = mTextView.getCompoundDrawables()[LEFT];
                        if (drawableLeft != null && event.getRawX() <= (mTextView.getLeft() + drawableLeft.getBounds().width())) {
                            listener.onLeft(v, drawableLeft);
                            return true;
                        }

                        Drawable drawableRight = mTextView.getCompoundDrawables()[RIGHT];
                        if (drawableRight != null && event.getRawX() >= (mTextView.getRight() - drawableRight.getBounds().width())) {
                            listener.onRight(v, drawableRight);
                            return true;
                        }
                    }

                    break;
            }

            return false;
        }

    };
}
