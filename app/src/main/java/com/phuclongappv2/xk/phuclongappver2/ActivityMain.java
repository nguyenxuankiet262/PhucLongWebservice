package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.CartRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.FavoriteRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.SuggestDrinkRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.CartDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.DrinkRoomDatabase;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.FavoriteDateSource;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.SuggestDrinkDataSource;
import com.phuclongappv2.xk.phuclongappver2.Model.Rating;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMain extends AppCompatActivity implements RatingDialogListener {
    CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    AHBottomNavigation bottomNavigation;
    private FragmentHome homeFragment;
    private FragmentFavorite favoriteFragment;
    private FragmentLocation locationFragment;
    private FragmentNotification notificationFragment;
    private FragmentMore moreFragment;

    IPhucLongAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Common.checkInSearchActivity = 0;

        mService = Common.getAPI();

        homeFragment = new FragmentHome();
        favoriteFragment = new FragmentFavorite();
        locationFragment = new FragmentLocation();
        notificationFragment = new FragmentNotification();
        moreFragment = new FragmentMore();

        viewPager = (CustomViewPager) findViewById(R.id.fragment_content);
        viewPager.setOffscreenPageLimit(4);
        adapter = new ViewPagerAdapter (ActivityMain.this.getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(locationFragment);
        adapter.addFragment(notificationFragment);
        adapter.addFragment(moreFragment);
        viewPager.setAdapter(adapter);

        bottomNavigation = findViewById(R.id.NavBot);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("HOME", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("FAVORITE", R.drawable.ic_favorite_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("LOCATION", R.drawable.ic_location_on_black_24dp);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("NOTIFICATION", R.drawable.ic_notifications_green_24dp);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("MORE", R.drawable.ic_menu_black_24dp);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        bottomNavigation.setAccentColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#176939"));
        bottomNavigation.setInactiveColor(Color.parseColor("#b2b2b2"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
        //Toast.makeText(this,String.valueOf(Common.cartRepository.countCartItem()),Toast.LENGTH_LONG).show();

        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        viewPager.setCurrentItem(0,false);
                        Common.checkPosision = 1;
                        return true;
                    case 1:
                        viewPager.setCurrentItem(1,false);
                        Common.checkPosision = 2;
                        return true;
                    case 2:
                        viewPager.setCurrentItem(2,false);
                        Common.checkPosision = 3;
                        return true;
                    case 3:
                        viewPager.setCurrentItem(3,false);
                        Common.checkPosision = 4;
                        return true;
                    case 4:
                        viewPager.setCurrentItem(4,false);
                        Common.checkPosision = 5;
                        return true;
                    default:
                        return false;
                }
            }
        });
        //Init Database
        initDB();
        updateNotificationHomeIcon();
    }

    public void updateNotificationHomeIcon() {
        if(Common.CurrentUser != null) {
            if (TextUtils.isEmpty(Common.CurrentUser.getName()) || TextUtils.isEmpty(Common.CurrentUser.getAddress())) {
                if(Common.CurrentUser.getNoti_history() == 1){
                    bottomNavigation.setNotification("3", 4);
                }
                else{
                    bottomNavigation.setNotification("2", 4);
                }
            } else {
                if(Common.CurrentUser.getNoti_history() == 1){
                    bottomNavigation.setNotification("1", 4);
                }
                else{
                    bottomNavigation.setNotification("", 4);
                }
            }
        }
        else{
            bottomNavigation.setNotification("",4);
        }
    }

    private void initDB() {
        Common.drinkroomDatabase = DrinkRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.drinkroomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDateSource.getInstance(Common.drinkroomDatabase.favoriteDAO()));
    }

    @Override
    public void onBackPressed() {
        if (Common.checkPosision == 1 && Common.BackPressA > 0) {
            super.onBackPressed();
            Common.BackPressA--;
        } else if (Common.checkPosision == 3 && Common.BackPressB > 0) {
            super.onBackPressed();
            Common.BackPressB--;
        }
        else if (Common.checkPosision == 4 && Common.BackPressD > 0){
            super.onBackPressed();
            Common.BackPressD--;
        }
        else if (Common.checkPosision == 5 && Common.BackPressE > 0){
            super.onBackPressed();
            Common.BackPressE--;
        }
        else {
            if(Common.CurrentUser != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Bạn có muốn đăng xuất không?");
                builder.setCancelable(false);
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Chấp nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Common.isConnectedToInternet(ActivityMain.this)) {
                            Common.CurrentUser = null;
                            AccountKit.logOut();
                            final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(ActivityMain.this).build();
                            alertDialog.setMessage("Please waiting...");
                            alertDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    Toast.makeText(ActivityMain.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                                    recreate();
                                }
                            }, 2000);
                        }
                        else{
                            Toast.makeText(ActivityMain.this,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
            else{
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationHomeIcon();
    }

    public void updateNoti(){
        homeFragment.updateCartCount();
        locationFragment.updateCartCount();
        favoriteFragment.updateCartCount();
        moreFragment.updateCartCount();
        notificationFragment.updateCartCount();
    }


    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ActivityMain.this);
        progressDialog.setTitle("Submitting...");
        progressDialog.setMessage("Please wait for a minute!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final Rating rate = new Rating();
        rate.setDrinkID(Common.drinkID);
        rate.setRate(i);
        rate.setComment(s);
        Date date = new Date();
        rate.setDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
        if(Common.CurrentUser != null) {
            if (Common.isConnectedToInternet(getBaseContext())) {
                mService.getUser(Common.CurrentUser.getPhone()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.body().getActive() == 1){
                            mService.setRating(Common.CurrentUser.getPhone(), rate.getDrinkID(), rate.getRate(), rate.getComment(), rate.getDate())
                                    .enqueue(new Callback<Rating>() {
                                        @Override
                                        public void onResponse(Call<Rating> call, Response<Rating> response) {
                                            Rating rating = response.body();
                                            if (TextUtils.isEmpty(rating.getError_msg())) {
                                                progressDialog.dismiss();
                                                Toast.makeText(ActivityMain.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Rating> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Log.d("EEEE", t.getMessage());
                                            Toast.makeText(ActivityMain.this, "Lỗi bất ngờ!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(ActivityMain.this, "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "Bạn cần đăng nhập để thực hiện chức năng này!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
