package com.phuclongappv2.xk.phuclongappver2;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerAdapter;

public class ActivityMain extends AppCompatActivity {
    CustomViewPager viewPager;
    ViewPagerAdapter adapter;
    AHBottomNavigation bottomNavigation;
    private Fragment homeFragment;
    private Fragment favoriteFragment;
    private Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new FragmentHome();
        favoriteFragment = new FragmentFavorite();
        mapFragment = new FragmentMap();

        viewPager = (CustomViewPager) findViewById(R.id.fragment_content);
        viewPager.setOffscreenPageLimit(3);
        adapter = new ViewPagerAdapter (ActivityMain.this.getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(mapFragment);
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
                        return true;
                    case 1:
                        viewPager.setCurrentItem(1,false);
                        return true;
                    case 2:
                        viewPager.setCurrentItem(2,false);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
}
