package pers.jay.wanandroid.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import pers.jay.wanandroid.common.Const;
import pers.zjc.commonlibs.util.ToastUtils;

public class WebViewClient extends android.webkit.WebViewClient {

    private final String mUrl;
    private Context mContext;
    private String[] whitelist = new String[] { Const.Url.WAN_ANDROID, Const.Url.KEY_51CTO, Const.Url.KEY_CNBLOGS, Const.Url.KEY_CSDN, Const.Url.KEY_JIANSHU, Const.Url.KEY_JUEJIN, Const.Url.KEY_SEGMENTFAULT, Const.Url.KEY_WEIXIN, Const.Url.KEY_163, Const.Url.KEY_GITHUB };

    public WebViewClient(Context mContext, String url) {
        this.mContext = mContext;
        this.mUrl = url;
    }

    // 会在点击加载超链接时触发
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        return !mUrl.contains(url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //该方法在Build.VERSION_CODES.LOLLIPOP以后有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        /*if (null != request.getUrl()) {
            String host = request.getUrl().getHost();
            if (StringUtils.isEmpty(host)) {
                return true;
            }
            if (!mUrl.contains(host)) {
                // 只要满足白名单之一就释放
                for (String whiteSite : whitelist) {
                    if (host.contains(whiteSite)) {
                        return false;
                    }
                }
                ToastUtils.showShort("禁止跳转其他页面");
                return true;
            }
            return false;
        }
        return true;*/
        String host = request.getUrl().getHost();
        if (ADFilterTool.hasAd(mContext, host)) {
            ToastUtils.showShort("禁止跳转其他页面");
            return true;
        }
        return false;
    }

    // 拦截所有请求
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        boolean hasWhiteSite = false;
        url = url.toLowerCase();
        for (String whiteSite : whitelist) {
            if (url.contains(whiteSite)) {
                hasWhiteSite = true;
                break;
            }
        }
        if (!hasWhiteSite) {
            if (!ADFilterTool.hasAd(mContext, url)) {
                return super.shouldInterceptRequest(view, url);
            }
            else {
                return new WebResourceResponse(null, null, null);
            }
        }
        else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        // 方法一：根据已知访问站点白名单过滤
//        boolean hasWhiteSite = false;
        String host = request.getUrl().getHost();
//        for (String whiteSite : whitelist) {
//            if (host != null && host.contains(whiteSite)) {
//                hasWhiteSite = true;
//            }
//        }
//        if (hasWhiteSite) {
//            return super.shouldInterceptRequest(view, request);
//        }
//        else {
//            return new WebResourceResponse(null, null, null);
//        }
        // 方法二：使用AdBlock的通配符过滤（黑名单）
        if (ADFilterTool.hasAd(mContext, host)) {
            return new WebResourceResponse(null, null, null);
        }
        else {
            return super.shouldInterceptRequest(view, request);
        }
    }
}
