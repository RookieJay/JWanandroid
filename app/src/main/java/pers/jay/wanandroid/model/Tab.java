package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Tab implements Parcelable {

    public static final int TYPE_PARENT = 1;
    public static final int TYPE_CHILD = 2;

    /**
     * children : [{"children":[],"courseId":13,"id":60,"name":"Android Studio相关","order":1000,"parentChapterId":150,"userControlSetTop":false,"visible":1},{"children":[],"courseId":13,"id":169,"name":"gradle","order":1001,"parentChapterId":150,"userControlSetTop":false,"visible":1},{"children":[],"courseId":13,"id":269,"name":"官方发布","order":1002,"parentChapterId":150,"userControlSetTop":false,"visible":1}]
     * courseId : 13
     * id : 150
     * name : 开发环境
     * order : 1
     * parentChapterId : 0
     * userControlSetTop : false
     * visible : 1
     */

    private int courseId;
    private int id;
    private String name;
    private int order;
    private int parentChapterId;
    private boolean userControlSetTop;
    private int visible;
    private List<Tab> children;

    public int getCourseId() { return courseId;}

    public void setCourseId(int courseId) { this.courseId = courseId;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public int getOrder() { return order;}

    public void setOrder(int order) { this.order = order;}

    public int getParentChapterId() { return parentChapterId;}

    public void setParentChapterId(int parentChapterId) { this.parentChapterId = parentChapterId;}

    public boolean isUserControlSetTop() { return userControlSetTop;}

    public void setUserControlSetTop(
            boolean userControlSetTop) { this.userControlSetTop = userControlSetTop;}

    public int getVisible() { return visible;}

    public void setVisible(int visible) { this.visible = visible;}

    public List<Tab> getChildren() { return children;}

    public void setChildren(List<Tab> children) { this.children = children;}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.courseId);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.order);
        dest.writeInt(this.parentChapterId);
        dest.writeByte(this.userControlSetTop ? (byte)1 : (byte)0);
        dest.writeInt(this.visible);
        dest.writeList(this.children);
    }

    public Tab() {}

    protected Tab(Parcel in) {
        this.courseId = in.readInt();
        this.id = in.readInt();
        this.name = in.readString();
        this.order = in.readInt();
        this.parentChapterId = in.readInt();
        this.userControlSetTop = in.readByte() != 0;
        this.visible = in.readInt();
        this.children = new ArrayList<Tab>();
        in.readList(this.children, Tab.class.getClassLoader());
    }

    public static final Parcelable.Creator<Tab> CREATOR = new Parcelable.Creator<Tab>() {
        @Override
        public Tab createFromParcel(Parcel source) {return new Tab(source);}

        @Override
        public Tab[] newArray(int size) {return new Tab[size];}
    };
}
