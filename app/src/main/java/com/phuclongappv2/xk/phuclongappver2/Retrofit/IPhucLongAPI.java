package com.phuclongappv2.xk.phuclongappver2.Retrofit;

import com.phuclongappv2.xk.phuclongappver2.Model.Banner;
import com.phuclongappv2.xk.phuclongappver2.Model.Category;
import com.phuclongappv2.xk.phuclongappver2.Model.CheckUserResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface IPhucLongAPI {
    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> checkExistUser(@Field("phone") String phone);
    @FormUrlEncoded
    @POST("registeruser.php")
    Call<User> registerUser(@Field("phone") String phone,
                            @Field("name") String name,
                            @Field("address") String address,
                            @Field("birthday") String birthday);
    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUser(@Field("phone") String phone);
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
