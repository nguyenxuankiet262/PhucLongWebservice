package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;
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

    List<String> listDistrict;

    MaterialSpinner spinnerDistrict, spinnerCity;

    ArrayAdapter<String> districtAdapter;

    RelativeLayout empty_layout;

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

        spinnerDistrict = view.findViewById(R.id.spinner_district);
        spinnerCity = view.findViewById(R.id.spinner_city);
        empty_layout = view.findViewById(R.id.empty_location_layout);

        //Load into common map
        Common.coordinatesStringMap = new HashMap<>();
        loadStoreList(0);


        listDistrict = new ArrayList<>();
        listDistrict.add("Tất cả");
        for(int i = 0; i < 12; i++) {
            listDistrict.add("Quận " + (i + 1) + "");
        }
        listDistrict.add("Tân Bình");
        listDistrict.add("Tân Phú");
        listDistrict.add("Gò Vấp");
        listDistrict.add("Phú Nhuận");
        listDistrict.add("Bình Thạnh");
        districtAdapter = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,listDistrict);
        spinnerDistrict.setAdapter(districtAdapter);
        spinnerDistrict.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                loadStoreList(position);
            }
        });

        spinnerCity.setText("Hồ Chí Minh");

        super.onViewCreated(view, savedInstanceState);
    }

    private void loadStoreList(int vitri) {
        List<Store> all_stores = Common.CurrentStore;
        if(vitri != 0) {
            List<Store> stores = new ArrayList<>();
            for (int i = 0; i < all_stores.size(); i++) {
                String path = all_stores.get(i).getAddress();
                String segments[] = path.split(", ");
                String district = segments[segments.length - 2];
                if (listDistrict.get(vitri).equals(district)) {
                    Common.coordinatesStringMap.put(all_stores.get(i).getId(),
                            new Coordinates(Common.ConvertStringToDouble(all_stores.get(i).getLat())
                                    , Common.ConvertStringToDouble(all_stores.get(i).getLng())
                                    , all_stores.get(i).getAddress()));
                    stores.add(all_stores.get(i));
                }
            }
            if(stores.size() != 0){
                empty_layout.setVisibility(View.GONE);
            }
            else{
                empty_layout.setVisibility(View.VISIBLE);
            }
            displayStore(stores);
        }
        else{
            empty_layout.setVisibility(View.GONE);
            for (int i = 0; i < all_stores.size(); i++) {
                Common.coordinatesStringMap.put(all_stores.get(i).getId(),
                        new Coordinates(Common.ConvertStringToDouble(all_stores.get(i).getLat())
                                , Common.ConvertStringToDouble(all_stores.get(i).getLng())
                                , all_stores.get(i).getAddress()));
            }
            displayStore(all_stores);
        }

    }

    private void displayStore(List<Store> stores) {
        adapter = new StoreAdapter(getActivity(), stores);
        recyclerView.setAdapter(adapter);
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
