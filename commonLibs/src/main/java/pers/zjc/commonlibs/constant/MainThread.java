package pers.zjc.commonlibs.constant;

import android.os.Handler;
import android.os.Looper;

public class MainThread {

    private Handler mHandler;

    public MainThread() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    public void postDelayed(Runnable runnable, long delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    public void removeCallbacks(Runnable runnable) {
        mHandler.removeCallbacks(runnable);
    }
}
