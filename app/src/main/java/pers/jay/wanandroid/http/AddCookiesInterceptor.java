package pers.jay.wanandroid.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pers.jay.wanandroid.common.Const;

/**
 * 添加cookie拦截器，用于非首次请求
 */
public class AddCookiesInterceptor implements Interceptor {

    private Context mContext;

    public AddCookiesInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("Content-type", "application/json; charset=utf-8");
        String cookie = getCookie(request.url().toString(), request.url().host());
        if (!TextUtils.isEmpty(cookie)) {
            // 将 Cookie 添加到请求头
            builder.addHeader(Const.HttpConst.COOKIE_NAME, cookie);
        }
        return chain.proceed(builder.build());
    }

    private String getCookie(String url, String domain) {
        SharedPreferences sp = mContext.getSharedPreferences(Const.Local.COOKIE_PREF, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(domain) && (url.contains(Const.Url.COLLECTIONS_WEBSITE)
                || url.contains(Const.Url.UNCOLLECTIONS_WEBSITE)
                || url.contains(Const.Url.ARTICLE_WEBSITE)
                || url.contains(Const.Url.COLLECTIONS_ARTICLES)
                || url.contains(Const.Url.TODO_WEBSITE))
                || url.contains(Const.Url.PERSONAL_COIN)) {
            return sp.getString(domain, "");
        }
        return "";
    }
}
