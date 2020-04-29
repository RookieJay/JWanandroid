package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * @author ZJC
 */
public class Todo implements MultiItemEntity, Parcelable {

    /**
     * orderby 1:完成日期顺序；2.完成日期逆序；3.创建日期顺序；4.创建日期逆序(默认)；
     */
    public static final int DONE_DATE_ASC = 1;
    public static final int DONE_DATE_DESC = 2;
    public static final int CREATE_DATE_ASC = 3;
    public static final int CREATE_DATE_DESC = 4;

    /**
     * status 状态， 1-完成；0未完成; 默认全部展示；
     */
    public static final int STATUS_TODO = 0;
    public static final int STATUS_DONE = 1;

    /**
     * completeDate : null
     * completeDateStr :
     * content : 去取
     * date : 1576771200000
     * dateStr : 2019-12-20
     * id : 21316
     * priority : 0
     * status : 0
     * title : 哈哈
     * type : 0
     * userId : 12331
     */

    private long completeDate;
    private String completeDateStr;
    private String content;
    private long date;
    private String dateStr;
    private int id;
    private int priority;
    private int status;
    private String title;
    private int type;
    private int userId;
    private TodoType todoType;

    public long getCompleteDate() { return completeDate;}

    public void setCompleteDate(long completeDate) { this.completeDate = completeDate;}

    public String getCompleteDateStr() { return completeDateStr;}

    public void setCompleteDateStr(
            String completeDateStr) { this.completeDateStr = completeDateStr;}

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

    public long getDate() { return date;}

    public void setDate(long date) { this.date = date;}

    public String getDateStr() { return dateStr;}

    public void setDateStr(String dateStr) { this.dateStr = dateStr;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public int getPriority() { return priority;}

    public void setPriority(int priority) { this.priority = priority;}

    public int getStatus() { return status;}

    public void setStatus(int status) { this.status = status;}

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public int getUserId() { return userId;}

    public void setUserId(int userId) { this.userId = userId;}

    public TodoType getTodoTypeEnum() {
        return TodoType.parse(type);
    }

    public boolean important() {
        return priority == 1;
    }

    @Override
    public int getItemType() {
        return 2;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.completeDate);
        dest.writeString(this.completeDateStr);
        dest.writeString(this.content);
        dest.writeLong(this.date);
        dest.writeString(this.dateStr);
        dest.writeInt(this.id);
        dest.writeInt(this.priority);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.userId);
        dest.writeInt(this.todoType == null ? -1 : this.todoType.ordinal());
    }

    public Todo() {}

    protected Todo(Parcel in) {
        this.completeDate = in.readLong();
        this.completeDateStr = in.readString();
        this.content = in.readString();
        this.date = in.readLong();
        this.dateStr = in.readString();
        this.id = in.readInt();
        this.priority = in.readInt();
        this.status = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.userId = in.readInt();
        int tmpTodoType = in.readInt();
        this.todoType = tmpTodoType == -1 ? null : TodoType.values()[tmpTodoType];
    }

    public static final Parcelable.Creator<Todo> CREATOR = new Parcelable.Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel source) {return new Todo(source);}

        @Override
        public Todo[] newArray(int size) {return new Todo[size];}
    };
}
