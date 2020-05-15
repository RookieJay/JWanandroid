package pers.jay.wanandroid.model;

import java.util.List;

public class User {
    /**
     * admin : false
     * chapterTops : []
     * collectIds : [7484,2696,7654,5573,7958,8252,8227,8080,3365,2439,1467,3596,2897,979,8247,8438,8694]
     * email :
     * icon :
     * id : 12331
     * nickname : RookieJay
     * password :
     * token :
     * type : 0
     * username : RookieJay
     */

    private boolean admin;
    private String email;
    private String icon;
    private int id;
    private String nickname;
    private String password;
    private String token;
    private int type;
    private String username;
    private List<?> chapterTops;
    private List<Integer> collectIds;

    public boolean isAdmin() { return admin;}

    public void setAdmin(boolean admin) { this.admin = admin;}

    public String getEmail() { return email;}

    public void setEmail(String email) { this.email = email;}

    public String getIcon() { return icon;}

    public void setIcon(String icon) { this.icon = icon;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getNickname() { return nickname;}

    public void setNickname(String nickname) { this.nickname = nickname;}

    public String getPassword() { return password;}

    public void setPassword(String password) { this.password = password;}

    public String getToken() { return token;}

    public void setToken(String token) { this.token = token;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public String getUsername() { return username;}

    public void setUsername(String username) { this.username = username;}

    public List<?> getChapterTops() { return chapterTops;}

    public void setChapterTops(List<?> chapterTops) { this.chapterTops = chapterTops;}

    public List<Integer> getCollectIds() { return collectIds;}

    public void setCollectIds(List<Integer> collectIds) { this.collectIds = collectIds;}

    @Override
    public String toString() {
        return "User{" + "admin=" + admin + ", email='" + email + '\'' + ", icon='" + icon + '\'' + ", id=" + id + ", nickname='" + nickname + '\'' + ", password='" + password + '\'' + ", token='" + token + '\'' + ", type=" + type + ", username='" + username + '\'' + ", chapterTops=" + chapterTops + ", collectIds=" + collectIds + '}';
    }
}
