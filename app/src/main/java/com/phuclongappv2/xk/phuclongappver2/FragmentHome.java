package com.phuclongappv2.xk.phuclongappver2;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


    public FragmentHome() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mService = Common.getAPI();

        toolbar = view.findViewById(R.id.main_tool_bar);
        list_menu = view.findViewById(R.id.list_category);
        list_menu.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list_menu.setHasFixedSize(true);
        list_menu.setNestedScrollingEnabled(false);

        loadMenu();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Phúc Long");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.logo_header_2);
        toolbar.setTitleTextAppearance(getActivity(), R.style.RobotoBoldTextAppearance);

        //Slider
        sliderLayout = view.findViewById(R.id.slider);
        getBanner();
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
        HashMap<String,String> bannerMap = new HashMap<>();
        for(Banner item:banners){
            bannerMap.put(item.getName(),item.getImage());
        }
        for (String name:bannerMap.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Fade);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        View view = menu.findItem(R.id.icon_cart_menu).getActionView();
        badge = view.findViewById(R.id.badge);
        cartBtn = view.findViewById(R.id.cart_icon);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(getActivity(), ActivityCart.class);
                startActivity(cartIntent);
            }
        });
        updateCartCount();
    }

    private void updateCartCount() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_search:
                Intent intent = new Intent(getActivity(),ActivitySearch.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
