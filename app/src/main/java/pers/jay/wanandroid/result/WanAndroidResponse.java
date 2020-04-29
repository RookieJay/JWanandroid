package pers.jay.wanandroid.result;

public class WanAndroidResponse<T> extends BaseWanBean {

    /**
     * data : {"admin":false,"chapterTops":[],"collectIds":[7484,2696,7654,5573,7958,8252,8227,8080,3365,2439,1467,3596,2897,979,8247,8438,8694],"email":"","icon":"","id":12331,"nickname":"RookieJay","password":"","token":"","type":0,"username":"RookieJay"}
     * errorCode : 0
     * errorMsg :
     */

    private T data;

    public T getData() { return data;}

    public void setData(T data) { this.data = data;}

}
