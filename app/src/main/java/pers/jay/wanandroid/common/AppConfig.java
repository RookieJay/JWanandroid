package pers.jay.wanandroid.common;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.utils.DarkModeUtils;
import pers.jay.wanandroid.utils.RvAnimUtils;
import pers.zjc.commonlibs.util.SPUtils;
import pers.zjc.commonlibs.util.StringUtils;

public class AppConfig {

    private String token;
    private String userName;
    private String account;
    private String password;
    private boolean isRemember;
    private boolean login;
    private int rvAnim = RvAnimUtils.RvAnim.NONE;
    private int darkModePosition = DarkModeUtils.POSITION_NIGHT_FOLLOW_SYSTEM;
    private String avatar;
    private String poem;

    private final SPUtils spUtils = SPUtils.getInstance("config");;

    private static class AppConfigHolder {
        private static final AppConfig INSTANCE = new AppConfig();
    }

    public static AppConfig getInstance() {
        return AppConfigHolder.INSTANCE;
    }

    private AppConfig() {
        token = spUtils.getString("token", token);
        userName = spUtils.getString("userName", userName);
        account = spUtils.getString("account", account);
        password = spUtils.getString("password", password);
        isRemember = spUtils.getBoolean("isRemember", isRemember);
        login = spUtils.getBoolean("login", login);
        rvAnim = spUtils.getInt("rvAnim", rvAnim);
        avatar = spUtils.getString("avatar", "");
        darkModePosition = spUtils.getInt("darkModePosition", darkModePosition);
        poem = spUtils.getString("poem", "");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        spUtils.put("token", token);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
        spUtils.put("account", account);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        spUtils.put("password", password);
    }

    public boolean isRemember() {
        return  isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
        spUtils.put("isRemember", isRemember);
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
        spUtils.put("login", login);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        spUtils.put("userName", userName);
    }

    public int getRvAnim() {
        return rvAnim;
    }

    public void setRvAnim(int rvAnim) {
        this.rvAnim = rvAnim;
        spUtils.put("rvAnim", rvAnim);
    }

    public int getDarkModePosition() {
        return darkModePosition;
    }

    public void setDarkModePosition(int darkModePosition) {
        this.darkModePosition = darkModePosition;
        spUtils.put("darkModePosition", darkModePosition);
    }

    public String getAvatar() {
        return avatar == null ? "" : avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        spUtils.put("avatar", avatar);
    }

    public String getPoem() {
        String s = StringUtils.isEmpty(poem) ? JApplication.getInstance().getResources().getString(R.string.app_name) : poem;
        return s;
    }

    public void setPoem(String poem) {
        if (StringUtils.isEmpty(poem)) {
            poem = JApplication.getInstance().getResources().getString(R.string.app_name);
        }
        this.poem = poem;
        spUtils.put("poem", poem);
    }

    public void clear() {
        spUtils.clear();
    }
}
