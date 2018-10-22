package com.phuclongappv2.xk.phuclongappver2;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerTitleAdapter;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHistory extends Fragment {
    ViewPagerTitleAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    public FragmentHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.BackPressE = 1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.tool_bar_history);
        tabLayout = view.findViewById(R.id.tab_bar_history);
        viewPager = view.findViewById(R.id.history_viewpager);
        adapter = new ViewPagerTitleAdapter(getActivity().getSupportFragmentManager(),4);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_fiber_new_green_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_local_shipping_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_check_circle_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_cancel_black_24dp);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconSelectedColor = ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark);
                tab.getIcon().setColorFilter(tabIconSelectedColor, PorterDuff.Mode.SRC_IN);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconUnselectedColor = ContextCompat.getColor(getActivity(), R.color.colorTextView);
                tab.getIcon().setColorFilter(tabIconUnselectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

}
