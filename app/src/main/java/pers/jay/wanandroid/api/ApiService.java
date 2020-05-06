package pers.jay.wanandroid.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 必应每日一图(郭霖)
     */
    @GET
    Observable<ResponseBody> bingImgUrl(@Url String url);

}
