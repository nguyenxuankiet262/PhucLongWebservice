package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.phuclongappv2.xk.phuclongappver2.FragmentHistoryDrink;
import com.phuclongappv2.xk.phuclongappver2.FragmentNotificationDetail;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerTitleAdapter extends FragmentStatePagerAdapter {
    int PAGE_COUNT;
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ViewPagerTitleAdapter(FragmentManager fm, int count) {
        super(fm);
        PAGE_COUNT = count;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(PAGE_COUNT == 4) {
            return FragmentHistoryDrink.newInstance(position);
        }
        else{
            return FragmentNotificationDetail.newInstance(position);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return null;
    }

    public void AddFragmentWithTitle(Fragment fragment){
        mFragmentList.add(fragment);
    }
}
