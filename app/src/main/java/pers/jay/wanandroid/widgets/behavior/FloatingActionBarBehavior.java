package pers.jay.wanandroid.widgets.behavior;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class FloatingActionBarBehavior extends FloatingActionButton.Behavior {

    private boolean isVisible = true;

    public FloatingActionBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull FloatingActionButton child,
                                       @NonNull View directTargetChild, @NonNull View target,
                                       int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                  @NonNull FloatingActionButton child, @NonNull View target, int dx,
                                  int dy, @NonNull int[] consumed, int type) {
        if (Math.abs(dy) < 10) {
            return;
        }
        if (dy > 0 && isVisible) {
            child.setVisibility(View.INVISIBLE);
            isVisible = false;
        } else if (dy < 0 && !isVisible) {
            child.show();
            isVisible = true;
        }
    }
}
