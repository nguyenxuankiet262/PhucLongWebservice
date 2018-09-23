package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;

public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView store_image;
    public TextView name_store, address_store, status_store;
    ItemClickListener itemClickListener;

    public StoreViewHolder(View itemView) {
        super(itemView);
        store_image = itemView.findViewById(R.id.image_store);
        status_store = itemView.findViewById(R.id.time_open);
        name_store = itemView.findViewById(R.id.name_store);
        address_store = itemView.findViewById(R.id.address_store);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
