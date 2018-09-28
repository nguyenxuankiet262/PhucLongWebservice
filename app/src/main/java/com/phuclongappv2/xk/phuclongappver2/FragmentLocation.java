package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.phuclongappv2.xk.phuclongappver2.Adapter.StoreAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Coordinates;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLocation extends Fragment {
    RecyclerView recyclerView;
    StoreAdapter adapter;

    IPhucLongAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentLocation(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mService = Common.getAPI();
        recyclerView = view.findViewById(R.id.location_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);;

        //Load into common map
        Common.coordinatesStringMap = new HashMap<String,Coordinates>();
        loadStoreList();
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadStoreList() {
        compositeDisposable.add(mService.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Store>>() {
                    @Override
                    public void accept(List<Store> stores) throws Exception {
                        displayStore(stores);
                    }
                }));
    }

    private void displayStore(List<Store> stores) {
        for(int i = 0 ; i < stores.size(); i++){
            Common.coordinatesStringMap.put(stores.get(i).getId(),
                    new Coordinates(Common.ConvertStringToDouble(stores.get(i).getLat())
                            ,Common.ConvertStringToDouble(stores.get(i).getLng())
                            ,stores.get(i).getAddress()));
        }
        adapter = new StoreAdapter(getActivity(),stores);
        recyclerView.setAdapter(adapter);
        //autoScroll();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    public void autoScroll(){
        final int speedScroll = 500;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if(count == adapter.getItemCount())
                    count =0;
                if(count < adapter.getItemCount()){
                    recyclerView.smoothScrollToPosition(++count);
                    handler.postDelayed(this,speedScroll);
                }
            }
        };
        handler.postDelayed(runnable,speedScroll);
    }

}
