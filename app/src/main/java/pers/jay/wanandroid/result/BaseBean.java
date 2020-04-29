package pers.jay.wanandroid.result;

public abstract class BaseBean<T> {

    private int code;

    private String msg;

    private T data;

    public abstract int getCode();

    public abstract String getMsg();

    public abstract T getData();
}
