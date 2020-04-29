package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Coin implements Parcelable {

    /**
     * coinCount : 4958
     * level : 50
     * rank : 1
     * userId : 20382
     * username : g**eii
     */

    private int coinCount;
    private int level;
    private int rank;
    private int userId;
    private String username;
    private String realRank;

    public String getIdStr() { return String.format("id:%s", userId == 0 ? "--" : userId);}

    public int getCoinCount() {
        return coinCount;
    }

    public int getLevel() {
        return level;
    }

    public void setCoinCount(int coinCount) { this.coinCount = coinCount;}

    public String getLevelStr() { return String.format("等级:%s", level == 0 ? "--" : level);}

    public void setLevel(int level) { this.level = level;}

    public int getRank() { return rank;}

    public String getFormatRank() {
        return String.format("排行:%s", rank == 0 ? "--" : rank);
    }

    public void setRank(int rank) { this.rank = rank;}

    public int getUserId() { return userId;}

    public void setUserId(int userId) { this.userId = userId;}

    public String getUsername() { return username;}

    public void setUsername(String username) { this.username = username;}

    public String getRealRank() {
        return realRank;
    }

    public void setRealRank(String realRank) {
        this.realRank = realRank;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.coinCount);
        dest.writeInt(this.level);
        dest.writeInt(this.rank);
        dest.writeInt(this.userId);
        dest.writeString(this.username);
        dest.writeString(this.realRank);
    }

    public Coin() {}

    protected Coin(Parcel in) {
        this.coinCount = in.readInt();
        this.level = in.readInt();
        this.rank = in.readInt();
        this.userId = in.readInt();
        this.username = in.readString();
        this.realRank = in.readString();
    }

    public static final Parcelable.Creator<Coin> CREATOR = new Parcelable.Creator<Coin>() {
        @Override
        public Coin createFromParcel(Parcel source) {return new Coin(source);}

        @Override
        public Coin[] newArray(int size) {return new Coin[size];}
    };
}
