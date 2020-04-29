package pers.jay.wanandroid.widgets.behavior;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<View> {

    private ObjectAnimator outAnimator, inAnimator; // 属性动画，用于垂直方向平移

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 垂直滑动
     * 这个方法主要用于监听协调布局的子view的滚动事件，当此方法返回true，表示要消耗此动作，
     * 继而执行下面的onNestedPreScroll方法，我们在代码中返回的是，滚动轴是不是竖直滚动轴。如果是的话，就返回true
     */
    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                       @NonNull View child, @NonNull View directTargetChild,
                                       @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    /**
     * 当用户上滑的时候，隐藏底部菜单栏，这里使用了动画退出，使用了ObjectAnimator.ofFloat方法，第一个是view对象，
     * 指的就是bottom，第二个是Y轴的变化，第三个是Y轴变化的多少，接下来设置动画秒数。
     */
    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child,
                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed,
                                  int type) {
        if (Math.abs(dy) < 10) {
            return;
        }
        if (dy > 0) {   // 上滑隐藏
            if (outAnimator == null) {
                outAnimator = ObjectAnimator.ofFloat(child, "translationY", 0, child.getHeight());
                outAnimator.setDuration(200);
            }
            if (!outAnimator.isRunning() && child.getTranslationY() <= 0) {
                outAnimator.start();
            }
        } else if (dy < 0) {    // 下滑显示
            if (inAnimator == null) {
                inAnimator = ObjectAnimator.ofFloat(child, "translationY", child.getHeight(), 0);
                inAnimator.setDuration(200);
            }
            if (!inAnimator.isRunning() && child.getTranslationY() >= child.getHeight()) {
                inAnimator.start();
            }

        }
    }

}
