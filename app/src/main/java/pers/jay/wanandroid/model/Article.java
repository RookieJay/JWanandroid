package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Article implements Parcelable {

    /**
     * apkLink :
     * author : MannaYang
     * chapterId : 97
     * chapterName : 音视频
     * collectInside : false
     * courseId : 13
     * desc :
     * envelopePic :
     * fresh : true
     * id : 8824
     * link : https://juejin.im/post/5d42d4946fb9a06ae439d46b
     * niceDate : 22分钟前
     * origin :
     * prefix :
     * projectLink :
     * publishTime : 1564713410000
     * superChapterId : 97
     * superChapterName : 多媒体技术
     * tags : []
     * title : Android 基于MediaCodec+MediaMuxer实现音视频录制合成
     * type : 0
     * userId : -1
     * visible : 1
     * zan : 0
     */

    @Id(autoincrement = true)
    private Long aId;
    private String apkLink;
    private String author;
    private int chapterId;
    private String chapterName;
    private boolean collect;
    private int courseId;
    private String desc;
    private String envelopePic;
    private boolean fresh;
    private int id;
    private String link;
    private String niceDate;
    private String origin;
    private String prefix;
    private String projectLink;
    private long publishTime;
    private int superChapterId;
    private String superChapterName;
    private String title;
    private int type;
    private int userId;
    private int visible;
    private int zan;
    @Transient
    private List<Tag> tags;
    private boolean isTop;
    private int selfVisible;
    private String shareUser;
    private int originId;

    public int getSelfVisible() {
        return selfVisible;
    }

    public void setSelfVisible(int selfVisible) {
        this.selfVisible = selfVisible;
    }

    public String getApkLink() { return apkLink;}

    public void setApkLink(String apkLink) { this.apkLink = apkLink;}

    public String getAuthor() { return author;}

    public void setAuthor(String author) { this.author = author;}

    public int getChapterId() { return chapterId;}

    public void setChapterId(int chapterId) { this.chapterId = chapterId;}

    public String getChapterName() { return chapterName;}

    public void setChapterName(String chapterName) { this.chapterName = chapterName;}

    public boolean isCollect() { return collect;}

    public void setCollect(boolean collect) { this.collect = collect;}

    public int getCourseId() { return courseId;}

    public void setCourseId(int courseId) { this.courseId = courseId;}

    public String getDesc() { return desc;}

    public void setDesc(String desc) { this.desc = desc;}

    public String getEnvelopePic() { return envelopePic;}

    public void setEnvelopePic(String envelopePic) { this.envelopePic = envelopePic;}

    public boolean isFresh() { return fresh;}

    public void setFresh(boolean fresh) { this.fresh = fresh;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getLink() { return link;}

    public void setLink(String link) { this.link = link;}

    public String getNiceDate() { return niceDate;}

    public void setNiceDate(String niceDate) { this.niceDate = niceDate;}

    public String getOrigin() { return origin;}

    public void setOrigin(String origin) { this.origin = origin;}

    public String getPrefix() { return prefix;}

    public void setPrefix(String prefix) { this.prefix = prefix;}

    public String getProjectLink() { return projectLink;}

    public void setProjectLink(String projectLink) { this.projectLink = projectLink;}

    public long getPublishTime() { return publishTime;}

    public void setPublishTime(long publishTime) { this.publishTime = publishTime;}

    public int getSuperChapterId() { return superChapterId;}

    public void setSuperChapterId(int superChapterId) { this.superChapterId = superChapterId;}

    public String getSuperChapterName() { return superChapterName;}

    public void setSuperChapterName(String superChapterName) { this.superChapterName = superChapterName;}

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public int getUserId() { return userId;}

    public void setUserId(int userId) { this.userId = userId;}

    public int getVisible() { return visible;}

    public void setVisible(int visible) { this.visible = visible;}

    public int getZan() { return zan;}

    public void setZan(int zan) { this.zan = zan;}

    public List<Tag> getTags() { return tags;}

    public void setTags(List<Tag> tags) { this.tags = tags;}

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getShareUser() {
        return shareUser;
    }

    public void setShareUser(String shareUser) {
        this.shareUser = shareUser;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apkLink);
        dest.writeString(this.author);
        dest.writeInt(this.chapterId);
        dest.writeString(this.chapterName);
        dest.writeByte(this.collect ? (byte)1 : (byte)0);
        dest.writeInt(this.courseId);
        dest.writeString(this.desc);
        dest.writeString(this.envelopePic);
        dest.writeByte(this.fresh ? (byte)1 : (byte)0);
        dest.writeInt(this.id);
        dest.writeString(this.link);
        dest.writeString(this.niceDate);
        dest.writeString(this.origin);
        dest.writeString(this.prefix);
        dest.writeString(this.projectLink);
        dest.writeLong(this.publishTime);
        dest.writeInt(this.superChapterId);
        dest.writeString(this.superChapterName);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.userId);
        dest.writeInt(this.visible);
        dest.writeInt(this.zan);
        dest.writeTypedList(this.tags);
        dest.writeByte(this.isTop ? (byte)1 : (byte)0);
        dest.writeInt(this.selfVisible);
        dest.writeString(this.shareUser);
        dest.writeInt(this.originId);
    }

    public Long getAId() {
        return this.aId;
    }

    public void setAId(Long aId) {
        this.aId = aId;
    }

    public boolean getCollect() {
        return this.collect;
    }

    public boolean getFresh() {
        return this.fresh;
    }

    public boolean getIsTop() {
        return this.isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    public Article() {}

    protected Article(Parcel in) {
        this.apkLink = in.readString();
        this.author = in.readString();
        this.chapterId = in.readInt();
        this.chapterName = in.readString();
        this.collect = in.readByte() != 0;
        this.courseId = in.readInt();
        this.desc = in.readString();
        this.envelopePic = in.readString();
        this.fresh = in.readByte() != 0;
        this.id = in.readInt();
        this.link = in.readString();
        this.niceDate = in.readString();
        this.origin = in.readString();
        this.prefix = in.readString();
        this.projectLink = in.readString();
        this.publishTime = in.readLong();
        this.superChapterId = in.readInt();
        this.superChapterName = in.readString();
        this.title = in.readString();
        this.type = in.readInt();
        this.userId = in.readInt();
        this.visible = in.readInt();
        this.zan = in.readInt();
        this.tags = in.createTypedArrayList(Tag.CREATOR);
        this.isTop = in.readByte() != 0;
        this.selfVisible = in.readInt();
        this.shareUser = in.readString();
        this.originId = in.readInt();
    }

    @Generated(hash = 961690116)
    public Article(Long aId, String apkLink, String author, int chapterId, String chapterName, boolean collect,
            int courseId, String desc, String envelopePic, boolean fresh, int id, String link, String niceDate,
            String origin, String prefix, String projectLink, long publishTime, int superChapterId,
            String superChapterName, String title, int type, int userId, int visible, int zan, boolean isTop,
            int selfVisible, String shareUser, int originId) {
        this.aId = aId;
        this.apkLink = apkLink;
        this.author = author;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.collect = collect;
        this.courseId = courseId;
        this.desc = desc;
        this.envelopePic = envelopePic;
        this.fresh = fresh;
        this.id = id;
        this.link = link;
        this.niceDate = niceDate;
        this.origin = origin;
        this.prefix = prefix;
        this.projectLink = projectLink;
        this.publishTime = publishTime;
        this.superChapterId = superChapterId;
        this.superChapterName = superChapterName;
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.visible = visible;
        this.zan = zan;
        this.isTop = isTop;
        this.selfVisible = selfVisible;
        this.shareUser = shareUser;
        this.originId = originId;
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {return new Article(source);}

        @Override
        public Article[] newArray(int size) {return new Article[size];}
    };
}
