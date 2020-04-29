package pers.jay.wanandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import pers.jay.wanandroid.R;
import timber.log.Timber;

public class UIUtils {

    public static AlertDialog createConfirmDialog(Context context, String message,
                                                  boolean cancelable,
                                                  DialogInterface.OnClickListener positiveListener,
                                                  DialogInterface.OnClickListener negativeListener) {
        return new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.title_warm_tips)) //标题
                                               .setMessage(message)//内容
                                               //                .setIcon(R.drawable.bg_image_placeholder)//图标
                                               .setCancelable(cancelable) //可否取消
                                               .setPositiveButton("确认", positiveListener)
                                               .setNegativeButton("取消", negativeListener)
                                               .create();
    }

    //判断当前View 是否处于touch中
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        return !(ev.getX() < x) && !(ev.getX() > (x + view.getWidth())) && !(ev.getY() < y) && !(ev.getY() > (y + view
                .getHeight()));
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 设置状态栏和导航栏透明
     */
    public static void setTrans(Window window) {
        window.requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 隐藏状态栏和导航栏
     */
    public static void hideStatusAndNavBar(Window window) {
        if (Build.VERSION.SDK_INT >= 21) {  //当系统版本大于5.0时执行
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE //两个FLAG一起用表示会让应用的主体内容占用系统状态栏的空间
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;    //让应用的主体内容占用系统导航栏的空间
            decorView.setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.TRANSPARENT);   //设置导航栏透明
            window.setStatusBarColor(Color.TRANSPARENT);   //设置状态栏透明
        }
    }

    public static void setSameColorBar(Boolean isLight, Window window, Resources resources) {
        if (Build.VERSION.SDK_INT >= 21) {
            //LAYOUT_FULLSCREEN 、LAYOUT_STABLE：让应用的主体内容占用系统状态栏的空间；
            //            View decorView = getWindow().getDecorView();
            //            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            //            decorView.setSystemUiVisibility(option);
            //            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            if (isLight) {
                window.setStatusBarColor(resources.getColor(R.color.colorPrimary));
                //                window.setStatusBarColor(Color.TRANSPARENT);
            } else {
                window.setStatusBarColor(resources.getColor(R.color.colorAccent));
            }
            //状态栏颜色接近于白色，文字图标变成黑色
            View decor = window.getDecorView();
            int ui = decor.getSystemUiVisibility();
            if (isLight) {
                //light --> a|=b的意思就是把a和b按位或然后赋值给a,   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                //dark  --> &是位运算里面，与运算,  a&=b相当于 a = a&b,  ~非运算符
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decor.setSystemUiVisibility(ui);
        }
    }

    public static int getColor(Context context, int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public static Drawable getDrawable(Context context, int resId) {
        return ContextCompat.getDrawable(context, resId);
    }

    /**
     * EditText获取焦点光标移到最后，并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        /**
         * Android加载刷新UI的时候，是从左到右，从上到下的顺序，正在加载的过程中，如果此时requestFocus(),的话，有可能此时还没把整个界面刷新好，导致requestFocus无效。
         * 故延迟加载
         */
        editText.postDelayed(() -> {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            editText.setSelection(editText.getText().toString().length()); //移动光标到最后
        }, 300);
        // 强制打开键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);

    }

    /**
     * RenderScript高斯模糊
     * Note：缩小的系数应该为2的整数次幂 ，即上面代码中的scale应该为1/2、1/4、1/8 ...
     * 参考BitmapFactory.Options 对图片缩放 的inSample系数。据前辈们经验，一般scale = 1/8 为佳。
     */
    public static Bitmap rsBlur(Context context, Bitmap source, int radius, float scale){
        Timber.d("UiUtils "+"origin size:"+source.getWidth()+"*"+source.getHeight());
        int width = Math.round(source.getWidth() * scale);
        int height = Math.round(source.getHeight() * scale);
        Bitmap inputBmp = Bitmap.createScaledBitmap(source,width,height,false);
        RenderScript renderScript =  RenderScript.create(context);
        Timber.i("UiUtils " +"scale size:"+inputBmp.getWidth()+"*"+inputBmp.getHeight());
        // Allocate memory for Renderscript to work with
        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        // Set the blur radius
        // ** 设置模糊半径**：设置一个模糊的半径,其值为 0－25
        scriptIntrinsicBlur.setRadius(radius);
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        renderScript.destroy();
        return inputBmp;
    }
}
