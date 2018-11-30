package com.phuclongappv2.xk.phuclongappver2;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nex3z.notificationbadge.NotificationBadge;
import com.phuclongappv2.xk.phuclongappver2.Adapter.ViewPagerTitleAdapter;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotification extends Fragment {
    private Toolbar toolbar;
    //Notification
    NotificationBadge badge;
    ImageView cartBtn;


    ViewPagerTitleAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    public FragmentNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.main_tool_bar);
        toolbar.inflateMenu(R.menu.menu_main_toolbar);
        Menu menu = toolbar.getMenu();
        View item = menu.findItem(R.id.icon_cart_menu).getActionView();
        badge = item.findViewById(R.id.badge);
        cartBtn = item.findViewById(R.id.cart_icon);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(getActivity(), ActivityCart.class);
                startActivity(cartIntent);
            }
        });
        updateCartCount();


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.icon_search)
                {
                    Intent intent = new Intent(getActivity(),ActivitySearch.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        tabLayout = view.findViewById(R.id.tab_bar_notification);
        viewPager = view.findViewById(R.id.notification_viewpager);
        adapter = new ViewPagerTitleAdapter(getActivity().getSupportFragmentManager(),2);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_notifications_active_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.discount_24dp_icon);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconSelectedColor = ContextCompat.getColor(getActivity(), R.color.colorWhite);
                tab.getIcon().setColorFilter(tabIconSelectedColor, PorterDuff.Mode.SRC_IN);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconUnselectedColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
                tab.getIcon().setColorFilter(tabIconUnselectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void updateCartCount() {
        if (badge == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.cartRepository.countCartItem() == 0)
                    badge.setVisibility(View.INVISIBLE);
                else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItem()));
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }
}
