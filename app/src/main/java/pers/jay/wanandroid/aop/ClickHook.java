//package pers.jay.wanandroid.aop;
//
//import android.util.Log;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//
//import timber.log.Timber;
//
//public class ClickHook {
//
//    private static Long sLastclick = 0L;
//    private static final Long FILTER_TIMEM = 1000L;
//
//    @Around("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void clickFilterHook(ProceedingJoinPoint joinPoint) {
//        if (System.currentTimeMillis() - sLastclick >= FILTER_TIMEM) {
//            sLastclick = System.currentTimeMillis();
//            try {
//                joinPoint.proceed();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        } else {
//            Timber.e("ClickFilterHook重复点击,已过滤");
//        }
//    }
//
//}
