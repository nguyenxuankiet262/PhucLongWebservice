package com.phuclongappv2.xk.phuclongappver2.Retrofit;

import com.phuclongappv2.xk.phuclongappver2.Model.Banner;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

public interface IPhucLongAPI {
    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();
}
