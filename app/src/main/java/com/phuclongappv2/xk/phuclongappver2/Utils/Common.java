package com.phuclongappv2.xk.phuclongappver2.Utils;

import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.RetrofitClient;

public class Common {
    private static final String BASE_URL = "http://10.0.2.2/phuclong/";

    public static IPhucLongAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IPhucLongAPI.class);
    }
}
