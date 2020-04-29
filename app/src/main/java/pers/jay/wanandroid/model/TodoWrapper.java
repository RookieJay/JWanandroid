package pers.jay.wanandroid.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class TodoWrapper implements MultiItemEntity {

    private long date;
    private String dateStr;
    private Todo todo;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    @Override
    public int getItemType() {
        return 2;
    }
}
