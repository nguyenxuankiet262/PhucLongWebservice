package com.phuclongappv2.xk.phuclongappver2;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.nex3z.notificationbadge.NotificationBadge;
import com.phuclongappv2.xk.phuclongappver2.Adapter.CategoryAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Banner;
import com.phuclongappv2.xk.phuclongappver2.Model.Category;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.phuclongappv2.xk.phuclongappver2.Utils.Common.isConnectedToInternet;

public class FragmentHome extends Fragment {

    private Toolbar toolbar;
    private RecyclerView list_menu;
    //Notification
    NotificationBadge badge;
    ImageView cartBtn;

    //Slider
    SliderLayout sliderLayout;

    //Adapter
    CategoryAdapter adapter;

    IPhucLongAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    SwipeRefreshLayout swipeRefreshLayout;


    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mService = Common.getAPI();

        toolbar = view.findViewById(R.id.main_tool_bar);
        toolbar.inflateMenu(R.menu.menu_main_toolbar);
        //Slider
        sliderLayout = view.findViewById(R.id.slider);
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
        list_menu = view.findViewById(R.id.list_category);
        list_menu.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list_menu.setHasFixedSize(true);
        list_menu.setNestedScrollingEnabled(false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity())) {
                    loadMenu();
                    getBanner();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getActivity())) {
                    loadMenu();
                    getBanner();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void getBanner() {
        compositeDisposable.add(mService.getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Banner>>() {
                    @Override
                    public void accept(List<Banner> banners) throws Exception {
                        displayBanner(banners);
                    }
                }));
    }

    private void displayBanner(List<Banner> banners) {
        sliderLayout.removeAllSliders();
        for(int i = 0; i < banners.size(); i++){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView.description(banners.get(i).getName())
                    .image(banners.get(i).getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Fade);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
        sliderLayout.startAutoCycle();
    }

    private void loadMenu() {
        compositeDisposable.add(mService.getCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayCategory(categories);
                    }
                }));
    }

    private void displayCategory(List<Category> categories) {
        adapter = new CategoryAdapter(getActivity(), categories);
        list_menu.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
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
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }
}
