package com.phuclongappv2.xk.phuclongappver2.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.CartRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.FavoriteRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.SuggestDrinkRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.DrinkRoomDatabase;
import com.phuclongappv2.xk.phuclongappver2.Model.Coordinates;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.FCMClient;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IFCMService;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.RetrofitClient;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.RetrofitScalarsClient;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Common {
    public static final String BASE_URL = "https://phuclongvn.000webhostapp.com/";
    public static final String API_TOKEN_URL = "https://phuclongvn.000webhostapp.com/braintree/main.php";
    public static final String FCM_URL = "https://fcm.googleapis.com/";

    public static User CurrentUser = null;
    public static Token CurrentToken = null;
    public static int checkInSearchActivity = 0;
    public static List<Store> CurrentStore;

    public static DrinkRoomDatabase drinkroomDatabase;
    public static CartRepository cartRepository;
    public static FavoriteRepository favoriteRepository;
    public static SuggestDrinkRepository suggestDrinkRepository;

    public static int BackPressA = 0;
    public static int BackPressB = 0;
    public static int BackPressD = 0;
    public static int BackPressE = 0;
    public static int checkPosision = 1;

    public static int drinkID;

    public static View parentFavLayout;
    public static boolean checkDrinkFragmentOpen;

    public static Map<String,Coordinates> coordinatesStringMap;

    public static IPhucLongAPI getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IPhucLongAPI.class);
    }

    public static IPhucLongAPI getScalarsAPI(){
        return RetrofitScalarsClient.getScalarsClient(BASE_URL).create(IPhucLongAPI.class);
    }

    public static IFCMService getFCMService(){
        return FCMClient.getClient(FCM_URL).create(IFCMService.class);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            for(int i = 0; i<infos.length;i++) {
                if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String ConvertIntToMoney(int money){
        return NumberFormat.getNumberInstance(Locale.US).format(money) + " VNĐ";
    }
    public static double ConvertStringToDouble(String s){
        return Double.parseDouble(s);
    }

    public static boolean isLocationEnabled(Context context)
    {
        int locationMode = 0;
        String locationProviders;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
