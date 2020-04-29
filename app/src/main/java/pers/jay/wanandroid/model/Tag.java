package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tag implements Parcelable {

    /**
     * name : 项目
     * url : /project/list/1?cid=314
     */

    private String name;
    private String url;

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.url);
    }

    public Tag() {}

    protected Tag(Parcel in) {
        this.name = in.readString();
        this.url = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel source) {return new Tag(source);}

        @Override
        public Tag[] newArray(int size) {return new Tag[size];}
    };
}
