package com.phuclongappv2.xk.phuclongappver2;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.phuclongappv2.xk.phuclongappver2.Adapter.DrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDrink extends Fragment {

    RecyclerView list_drink;

    //Adapter
    DrinkAdapter adapter;

    //Extra
    String id_cate,name_cate;

    Toolbar toolbar;

    IPhucLongAPI mService;

    SwipeRefreshLayout swipeRefreshLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.BackPressA = 1;
        Common.checkDrinkFragmentOpen = true;
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mService = Common.getAPI();

        toolbar = view.findViewById(R.id.tool_bar_drink);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        list_drink = view.findViewById(R.id.listDrink);
        list_drink.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        list_drink.setHasFixedSize(true);


        if(getArguments() != null){
            id_cate = getArguments().getString("CategoryId");
            name_cate = getArguments().getString("CategoryName");
        }
        if(!id_cate.isEmpty() && id_cate != null){
            toolbar.setTitle(name_cate);
        }

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_drink);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity())) {
                    loadDrink(id_cate);
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
                    loadDrink(id_cate);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void loadDrink(String id_cate) {
        compositeDisposable.add(mService.getDrink(id_cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        if(drinks.size() != 0) {
                            displayDrink(drinks);
                        }
                    }
                }));
    }
    private void displayDrink(List<Drink> drinks) {
        adapter = new DrinkAdapter(getActivity(), drinks,getActivity());
        list_drink.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
