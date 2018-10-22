package com.phuclongappv2.xk.phuclongappver2.Retrofit;

import com.phuclongappv2.xk.phuclongappver2.Model.AverageRate;
import com.phuclongappv2.xk.phuclongappver2.Model.Banner;
import com.phuclongappv2.xk.phuclongappver2.Model.Category;
import com.phuclongappv2.xk.phuclongappver2.Model.CheckUserResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Model.Order;
import com.phuclongappv2.xk.phuclongappver2.Model.Rating;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
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
                            @Field("address") String address);
    @FormUrlEncoded
    @POST("inserttoken.php")
    Call<Token> insertToken(@Field("phone") String phone,
                            @Field("token") String token,
                            @Field("isServerToken") int isServerToken);
    @FormUrlEncoded
    @POST("updateuser.php")
    Call<User> updateUser(@Field("phone") String phone,
                            @Field("name") String name,
                            @Field("address") String address);@FormUrlEncoded
    @POST("updatehistory.php")
    Call<User> updateHistory(@Field("phone") String phone,
                          @Field("noti_history") int noti_history);
    @FormUrlEncoded
    @POST("setrating.php")
    Call<Rating> setRating(@Field("userID") String userID,
                           @Field("drinkID") int drinkID,
                           @Field("rate") int rate,
                           @Field("comment") String comment,
                           @Field("date") String date);
    @FormUrlEncoded
    @POST("insertorder.php")
    Call<String> insertOrder(@Field("address") String address,
                                @Field("name") String name,
                                @Field("note") String note,
                                @Field("payment") String payment,
                                @Field("phone") String phone,
                                @Field("price") String price,
                                @Field("timeorder") String timeorder,
                                @Field("drinkdetail") String drinkdetail,
                                @Field("status") int status,
                                @Field("storeID") int storeID);
    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUser(@Field("phone") String phone);
    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") int isServerToken);
    @FormUrlEncoded
    @POST("getdrink.php")
    Observable<List<Drink>> getDrink(@Field("menuid") String menuid);
    @FormUrlEncoded
    @POST("getrating.php")
    Observable<List<Rating>> getRating(@Field("drinkID") int drinkID);
    @FormUrlEncoded
    @POST("getavgrate.php")
    Call<AverageRate> getAvgRate(@Field("drinkID") int drinkID);
    @FormUrlEncoded
    @POST("getdrinkbyname.php")
    Observable<List<Drink>> getDrinkByName(@Field("s") String s);
    @FormUrlEncoded
    @POST("getorderbystatus.php")
    Observable<List<Order>> getOrderByStatus(@Field("phone") String phone,
                                             @Field("status") int status);
    @GET("gettenrandomdrink.php")
    Observable<List<Drink>> getTenRandomDrinks();
    @GET("getbanner.php")
    Observable<List<Banner>> getBanner();
    @GET("getcategory.php")
    Observable<List<Category>> getCategory();
    @GET("getlocation.php")
    Observable<List<Store>> getLocation();
}
