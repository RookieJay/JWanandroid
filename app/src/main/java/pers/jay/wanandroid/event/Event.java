package pers.jay.wanandroid.event;


public class Event<T> {

    public Event(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    private int eventCode;

    private T data;

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
