package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;
import com.phuclongappv2.xk.phuclongappver2.FragmentMap;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.StoreViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreViewHolder> {
    Context context;
    List<Store> storeList;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("h a");

    public StoreAdapter(Context context, List<Store> storeList){
        this.context = context;
        this.storeList = storeList;
    }
    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_location_layout,parent,false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoreViewHolder holder, final int position) {
        Picasso.with(context).load(storeList.get(position).getImage()).into(holder.store_image);
        holder.name_store.setText(storeList.get(position).getName());
        holder.address_store.setText(storeList.get(position).getAddress());

        try {
            Date EndTime = dateFormat.parse("10 PM");
            Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));
            if (CurrentTime.after(EndTime))
            {
                holder.status_store.setText("Đóng cửa");
                holder.status_store.setTextColor(ContextCompat.getColor(context,R.color.colorCancel));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("StoreID", storeList.get(position).getId());
                FragmentMap mapFragment = new FragmentMap();
                mapFragment.setArguments(bundle);
                FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.store_layout,mapFragment).addToBackStack(null).commit();
                //Toast.makeText(context, "ID = " + storeList.get(position).getId() + " " + Common.coordinatesStringMap.get(storeList.get(position).getId()).getAddress() +" và tọa độ là "
                        //+ Common.coordinatesStringMap.get(storeList.get(position).getId()).getLat()+"",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }
}
