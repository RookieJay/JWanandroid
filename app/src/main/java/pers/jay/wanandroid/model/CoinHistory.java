package pers.jay.wanandroid.model;

public class CoinHistory {

    /**
     * coinCount : 28
     * date : 1576115152000
     * desc : 2019-12-12 09:45:52 签到 , 积分：13 + 15
     * id : 112434
     * reason : 签到
     * type : 1
     * userId : 12331
     * userName : RookieJay
     */

    private int coinCount;
    private long date;
    private String desc;
    private int id;
    private String reason;
    private int type;
    private int userId;
    private String userName;

    public int getCoinCount() { return coinCount;}

    public void setCoinCount(int coinCount) { this.coinCount = coinCount;}

    public long getDate() { return date;}

    public void setDate(long date) { this.date = date;}

    public String getDesc() { return desc;}

    public void setDesc(String desc) { this.desc = desc;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getReason() { return reason;}

    public void setReason(String reason) { this.reason = reason;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public int getUserId() { return userId;}

    public void setUserId(int userId) { this.userId = userId;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}
}
