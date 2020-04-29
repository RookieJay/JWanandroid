package pers.jay.wanandroid.utils;

import android.text.Html;

public final class JUtils {

    public static String html2String(String str) {
        return Html.fromHtml(str).toString();
    }


}
