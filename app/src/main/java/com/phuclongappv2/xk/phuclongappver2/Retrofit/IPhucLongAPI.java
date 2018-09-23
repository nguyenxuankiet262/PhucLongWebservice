package com.phuclongappv2.xk.phuclongappver2.Retrofit;

import com.phuclongappv2.xk.phuclongappver2.Model.Banner;
import com.phuclongappv2.xk.phuclongappver2.Model.Category;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IPhucLongAPI {
    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuid);
    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();
    @GET("getcategory.php")
    Observable<List<Category>> getCategory();
    @GET("getlocation.php")
    Observable<List<Store>> getLocation();
}
