package pers.jay.wanandroid.http;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import pers.jay.wanandroid.common.Const;
import pers.zjc.commonlibs.util.StringUtils;

/**
 * 获取cookie拦截器，用于首次请求
 */
public class SaveCookiesInterceptor implements Interceptor {

    private static final String COOKIE_PREF = "cookies_prefs";
    private Context mContext;

    SaveCookiesInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        //set-cookie可能为多个
        if (!response.headers(Const.HttpConst.SET_COOKIE).isEmpty()) {
            List<String> cookies = response.headers(Const.HttpConst.SET_COOKIE);
            String cookie = encodeCookie(cookies);
            saveCookie(request.url().toString(), request.url().host(), cookie);
        }
        return response;
    }

    //整合cookie为唯一字符串
    private String encodeCookie(List<String> cookies) {
        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();
        for (String cookie : cookies) {
            String[] arr = cookie.split(";");
            for (String s : arr) {
                if (set.contains(s)) { continue; }
                set.add(s);

            }
        }

        for (String cookie : set) {
            sb.append(cookie).append(";");
        }

        int last = sb.lastIndexOf(";");
        if (sb.length() - 1 == last) {
            sb.deleteCharAt(last);
        }

        return sb.toString();
    }

    //保存cookie到本地，这里我们分别为该url和host设置相同的cookie，其中host可选
    //这样能使得该cookie的应用范围更广
    private void saveCookie(String url, String domain, String cookies) {
        SharedPreferences sp = mContext.getSharedPreferences(COOKIE_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        // 如果 response 的header 中包含 cookie 信息。并且此次请求是登录或者注册才保存cookie
        if (!StringUtils.isEmpty(url) && (url.contains(Const.Url.URL_LOGIN_KEY) || url.contains(
                Const.Url.URL_LOGIN_REGISTER))) {
            editor.putString(url, cookies);
            editor.putString(domain, cookies);
        }
        editor.apply();

    }
}