package pers.jay.wanandroid.common;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

public interface Const {

    String APP_ID = "702cce47ce";

    interface Url {
        String DAILY_BING = "http://cn.bing.com/th?id=OHR.BourgesAerial_ROW9185097510_1920x1080.jpg&rf=LaDigue_1920x1081920x1080.jpg";
        String DAILY_BING_1  = "https://api.xygeng.cn/Bing";
        String DAILY_BING_GUOLIN = "http://guolin.tech/api/bing_pic";
        String GUOLIN = "http://guolin.tech/";
        String WAN_ANDROID = "https://www.wanandroid.com/";
        String GITHUB = "https://api.github.com/";
        String ABOUT_US = WAN_ANDROID + "about";

        String URL_LOGIN_KEY  = "user/login";
        String URL_LOGIN_REGISTER  = "user/register";

        String COLLECTIONS_ARTICLES = "lg/collect";
        String COLLECTIONS_WEBSITE = "lg/unCollect";
        String UNCOLLECTIONS_WEBSITE = "lg/uncollect";
        String ARTICLE_WEBSITE = "article";
        String TODO_WEBSITE = "lg/todo";
        String PERSONAL_COIN = "coin";

        // webView白名单
        String KEY_CSDN = "csdn";
        String KEY_JUEJIN = "juejin";
        String KEY_WEIXIN = "weixin";
        String KEY_CNBLOGS = "cnblogs";
        String KEY_JIANSHU = "jianshu";
        String KEY_51CTO = "51cto";
        String KEY_SEGMENTFAULT = "segmentfault";
        String KEY_163 = "163.com";
        String KEY_GITHUB = "github.com";

        String BLANK = "about:blank";
    }

    interface HttpConst {
        //所有的返回结构均为上述，其中errorCode如果为负数则认为错误，此时errorMsg会包含错误信息。
        // data为Object，返回数据根据不同的接口而变化
        //errorCode = 0 代表执行成功，不建议依赖任何非0的 errorCode.
        //errorCode = -1001 代表登录失效或未登录，需要重新登录。
        int HTTP_CODE_SUCCESS = 0;
        int HTTP_CODE_LOGIN_EXPIRED = -1001;
        int HTTP_CODE_ERROR = -1;
        long DEFAULT_TIMEOUT = 5000;
        String COOKIE_NAME = "Cookie";
        String SET_COOKIE = "Set-cookie";

    }

    interface Key {
        String KEY_CONTAINER_TYPE = "KEY_CONTAINER_TYPE";
        String KEY_WEB_PAGE_DATA = "KEY_WEB_PAGE_DATA";
        String KEY_WEB_PAGE_TYPE = "KEY_WEB_PAGE_TYPE";
        String KEY_WEB_PAGE_URL = "KEY_WEB_PAGE_URL";
        String KEY_WEB_PAGE_TITLE = "KEY_WEB_PAGE_TITLE";
        String KEY_TAB_DATA = "KEY_TAB_DATA";
        String KEY_POSITION = "KEY_POSITION";
        String KEY_TAB_FROM_TYPE = "KEY_TAB_FROM_TYPE";
        String KEY_TAB_CHILD_POSITION = "KEY_TAB_CHILD_POSITION";
        String KEY_LOGIN_SUCCESS = "KEY_LOGIN_SUCCESS";
        String KEY_COIN_COUNT = "KEY_COIN_COUNT";
        String KEY_FRAGMENT = "KEY_FRAGMENT";
        String KEY_TODO_TYPE = "KEY_TODO_TYPE";
        String KEY_TODO_CAT = "KEY_TODO_CAT";
        String KEY_TITLE = "KEY_TITLE";
        String KEY_TODO = "KEY_TODO";
        String KEY_START_TYPE = "KEY_START_TYPE";
        String KEY_MY_COIN = "KEY_MY_COIN";
        String SAVE_INSTANCE_STATE = "SAVE_INSTANCE_STATE";
        String KEY_USER_ID = "KEY_USER_ID";
    }

    interface Type {
        int TYPE_MAIN_FRAG = 0;
        int TYPE_TAB_KNOWLEDGE = 1;
        int TYPE_TAB_WEIXIN = 2;
        int TYPE_TAB_PROJECT = 3;
    }

    interface Local {
        String COOKIE_PREF = "cookies_prefs";
    }

    interface EventCode {
        int LOGIN_EXPIRED = -1001;
        int LOGIN_SUCCESS = 1;
        int LOG_OUT = 2;
        int COMMIT_SUCCESS = 3;
        int TODO_DONE = 4;
        int CHANGE_RV_ANIM = 5;
        int COLLECT_ARTICLE = 6;
        int CHANGE_UI_MODE = 7;
        int SHARE_SUCCESS = 8;
        int LOGIN_RETURN = 9;
        int SIGN_SUCCESS = 10;
    }

    @SuppressLint("SimpleDateFormat")
    interface DateFormat {

        SimpleDateFormat WITH_HMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat WITHOUT_HMS = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat WITHOUT_HMS_00 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat HHMM = new SimpleDateFormat("HH:mm");
        SimpleDateFormat HMM = new SimpleDateFormat("H:mm");
        SimpleDateFormat MMDDHHmm = new SimpleDateFormat("MM-dd HH:mm");
        SimpleDateFormat CN_M_D = new SimpleDateFormat("M月d日");
        SimpleDateFormat CN_MM_DD = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat CN_MD_H_m = new SimpleDateFormat("M月d日 H时m分");
        SimpleDateFormat CN_WITHOUT_HMS = new SimpleDateFormat("yyyy年MM月dd日");
        SimpleDateFormat WITHOUT_HMS_59 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        SimpleDateFormat HH_MM_SS = new SimpleDateFormat("HH时mm分ss秒");
    }
    
    interface Animation {
         int ALPHAIN = 1;
         int SCALEIN = 2;
         int SLIDEIN_BOTTOM = 3;
         int SLIDEIN_LEFT = 4;
         int SLIDEIN_RIGHT = 5;
    }
}
