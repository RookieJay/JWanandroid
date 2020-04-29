package pers.jay.wanandroid.utils;

import android.support.v7.app.AppCompatDelegate;

public class DarkModeUtils {

    // Always use the day (light) theme(一直应用日间(light)主题).1
    public static final int POSITION_NIGHT_NO = 0;
    // MODE_NIGHT_YES. Always use the night (dark) theme(一直使用夜间(dark)主题).2
    public static final int POSITION_NIGHT_YES = 1;
    // MODE_NIGHT_AUTO. Changes between day/night based on the time of day(根据当前时间在day/night主题间切换).0
    public static final int POSITION_NIGHT_AUTO = 2;
    // MODE_NIGHT_FOLLOW_SYSTEM(默认选项).(跟随系统，通常为MODE_NIGHT_NO).-1
    public static final int POSITION_NIGHT_FOLLOW_SYSTEM = 3;

    public static String getName(int position) {
        switch (position) {
            case POSITION_NIGHT_NO:
                return "开启";
            case POSITION_NIGHT_YES:
                return "关闭";
            case POSITION_NIGHT_AUTO:
                return "自动(根据时间切换)";
            case POSITION_NIGHT_FOLLOW_SYSTEM:
            default:
                return "跟随系统";
        }
    }

    public static int getMode(int position) {
        int mode = DarkModeUtils.POSITION_NIGHT_FOLLOW_SYSTEM;
        switch (position) {
            case 0:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            case 1:
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case 2:
                mode = AppCompatDelegate.MODE_NIGHT_AUTO;
                break;
            case 3:
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
            default:
                break;
        }
        return mode;
    }
}
