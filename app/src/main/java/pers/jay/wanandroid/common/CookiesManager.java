package pers.jay.wanandroid.common;

import pers.zjc.commonlibs.util.SPUtils;


public class CookiesManager {

    private static SPUtils spUtils;

    private static class CookiesManagerHolder {
        private static final CookiesManager INSTANCE = new CookiesManager();
    }

    public static CookiesManager getInstance() {
        spUtils = SPUtils.getInstance(Const.Local.COOKIE_PREF);
        return CookiesManagerHolder.INSTANCE;
    }

    public void clearCookies() {
        spUtils.clear();
    }

}
