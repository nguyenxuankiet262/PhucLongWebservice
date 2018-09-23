package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    Toolbar toolbar;

    private static View view;
    private MapView mapView;
    private GoogleMap mMap;
    String id;
    private static final int ZoomValue = 15;

    private static final String name = "Phúc Long Coffee & Tea Express";

    private static final LatLng CN1 = new LatLng(Common.coordinatesStringMap.get("1").getLat()
            ,Common.coordinatesStringMap.get("1").getLng());
    private static final LatLng CN2 = new LatLng(Common.coordinatesStringMap.get("2").getLat()
            ,Common.coordinatesStringMap.get("2").getLng());
    private static final LatLng CN3 = new LatLng(Common.coordinatesStringMap.get("3").getLat()
            ,Common.coordinatesStringMap.get("3").getLng());
    private static final LatLng CN4 = new LatLng(Common.coordinatesStringMap.get("4").getLat()
            ,Common.coordinatesStringMap.get("4").getLng());
    private static final LatLng CN5 = new LatLng(Common.coordinatesStringMap.get("5").getLat()
            ,Common.coordinatesStringMap.get("5").getLng());

    private Marker mCN1;
    private Marker mCN2;
    private Marker mCN3;
    private Marker mCN4;
    private Marker mCN5;


    public FragmentMap() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.BackPressB = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentof
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null){
            id = getArguments().getString("StoreID");
        }
        mapView = (MapView) view.findViewById(R.id.map_store);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        toolbar = view.findViewById(R.id.tool_bar_map);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mCN1 = mMap.addMarker(new MarkerOptions()
                .position(CN1)
                .title(name)
                .snippet(Common.coordinatesStringMap.get("1").getAddress()));
        mCN1.setTag(0);

        mCN2 = mMap.addMarker(new MarkerOptions()
                .position(CN2)
                .title(name)
                .snippet(Common.coordinatesStringMap.get("2").getAddress()));
        mCN2.setTag(0);

        mCN3 = mMap.addMarker(new MarkerOptions()
                .position(CN3)
                .title(name)
                .snippet(Common.coordinatesStringMap.get("3").getAddress()));
        mCN3.setTag(0);

        mCN4 = mMap.addMarker(new MarkerOptions()
                .position(CN4)
                .title(name)
                .snippet(Common.coordinatesStringMap.get("4").getAddress()));
        mCN4.setTag(0);
        mCN5 = mMap.addMarker(new MarkerOptions()
                .position(CN5)
                .title(name)
                .snippet(Common.coordinatesStringMap.get("5").getAddress()));
        mCN5.setTag(0);

        //Zoom location selected store
        if(id.equals("1")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CN1,ZoomValue));
        }
        if(id.equals("2")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CN2,ZoomValue));
        }
        if(id.equals("3")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CN3,ZoomValue));
        }
        if(id.equals("4")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CN3,ZoomValue));
        }
        if(id.equals("5")){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CN4,ZoomValue));
        }
    }

}
