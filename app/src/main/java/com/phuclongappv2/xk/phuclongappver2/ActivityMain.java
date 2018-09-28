package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.facebook.accountkit.AccountKit;
import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.CartRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.FavoriteRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.SuggestDrinkRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.CartDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.DrinkRoomDatabase;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.FavoriteDateSource;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.SuggestDrinkDataSource;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

public class ActivityMain extends AppCompatActivity {
    CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    AHBottomNavigation bottomNavigation;
    private Fragment homeFragment;
    private Fragment favoriteFragment;
    private Fragment locationFragment;
    private Fragment moreFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new FragmentHome();
        favoriteFragment = new FragmentFavorite();
        locationFragment = new FragmentLocation();
        moreFragment = new FragmentMore();

        viewPager = (CustomViewPager) findViewById(R.id.fragment_content);
        viewPager.setOffscreenPageLimit(3);
        adapter = new ViewPagerAdapter (ActivityMain.this.getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(locationFragment);
        adapter.addFragment(moreFragment);
        viewPager.setAdapter(adapter);

        bottomNavigation = findViewById(R.id.NavBot);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("HOME", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("FAVORITE", R.drawable.ic_favorite_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("LOCATION", R.drawable.ic_location_on_black_24dp);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("MORE", R.drawable.ic_menu_black_24dp);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

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
                    default:
                        return false;
                }
            }
        });
        //Init Database
        initDB();
        updateNotificationHomeIcon();
    }


    public void setLoginPage(){
        bottomNavigation.setCurrentItem(3);
        viewPager.setCurrentItem(3,false);
        Common.checkPosision = 4;
    }

    public void updateNotificationHomeIcon() {
        if (Common.cartRepository.countCartItem() == 0)
            bottomNavigation.setNotification("", 0);
        else {
            bottomNavigation.setNotification(String.valueOf(Common.cartRepository.countCartItem()), 0);
        }
    }

    private void initDB() {
        Common.drinkroomDatabase = DrinkRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.drinkroomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDateSource.getInstance(Common.drinkroomDatabase.favoriteDAO()));
        Common.suggestDrinkRepository = SuggestDrinkRepository.getInstance(SuggestDrinkDataSource.getInstance(Common.drinkroomDatabase.suggestDrinkDAO()));
    }

    @Override
    public void onBackPressed() {
        if (Common.checkPosision == 1 && Common.BackPressA > 0) {
            super.onBackPressed();
            Common.BackPressA--;
        } else if (Common.checkPosision == 3 && Common.BackPressB > 0) {
            super.onBackPressed();
            Common.BackPressB--;
        } else {
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
                        Common.CurrentUser = null;
                        AccountKit.logOut();
                        recreate();
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


}
