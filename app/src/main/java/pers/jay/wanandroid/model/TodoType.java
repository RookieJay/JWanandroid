package pers.jay.wanandroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum TodoType implements Parcelable {

    WORK("1"), LIFE("2"), ENTERTAINMAINT("3"), UNKNOWN("999");

    private String type;

    TodoType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static final Creator<TodoType> CREATOR = new Creator<TodoType>() {
        @Override
        public TodoType createFromParcel(Parcel in) {
            return TodoType.values()[in.readInt()];
        }

        @Override
        public TodoType[] newArray(int size) {
            return new TodoType[size];
        }
    };

    public static TodoType parse(int type) {
        switch (type) {
            case 1:
                return WORK;
            case 2:
                return LIFE;
            case 3:
                return ENTERTAINMAINT;
            default:
                return UNKNOWN;

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(type);}
}
