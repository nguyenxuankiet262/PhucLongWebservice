package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.CartRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.FavoriteRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.CartDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.DrinkRoomDatabase;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.FavoriteDateSource;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

public class ActivityMain extends AppCompatActivity {
    CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    AHBottomNavigation bottomNavigation;
    private Fragment homeFragment;
    private Fragment favoriteFragment;
    private Fragment locationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new FragmentHome();
        favoriteFragment = new FragmentFavorite();
        locationFragment = new FragmentLocation();

        viewPager = (CustomViewPager) findViewById(R.id.fragment_content);
        viewPager.setOffscreenPageLimit(3);
        adapter = new ViewPagerAdapter (ActivityMain.this.getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(locationFragment);
        viewPager.setAdapter(adapter);

        bottomNavigation = findViewById(R.id.NavBot);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.ic_home_black_24dp);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Favorite", R.drawable.ic_favorite_black_24dp);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Location", R.drawable.ic_location_on_black_24dp);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setAccentColor(Color.parseColor("#FFFFFF"));
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#176939"));
        bottomNavigation.setInactiveColor(Color.parseColor("#b2b2b2"));

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
                    default:
                        return false;
                }
            }
        });
        //Init Database
        initDB();
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
        }
        else if(Common.checkPosision == 3 && Common.BackPressB >0) {
            super.onBackPressed();
            Common.BackPressB--;
        }
        else{
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

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
