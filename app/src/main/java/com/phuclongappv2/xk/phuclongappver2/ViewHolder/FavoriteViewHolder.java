package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemLongClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;

public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public ImageView image_product;
    public TextView name_drink, price_drink;
    ItemClickListener itemClickListener;
    ItemLongClickListener itemLongClickListener;

    public FavoriteViewHolder(View itemView) {
        super(itemView);
        image_product = itemView.findViewById(R.id.image_fav);
        name_drink = itemView.findViewById(R.id.name_fav);
        price_drink = itemView.findViewById(R.id.price_fav);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener, ItemLongClickListener itemLongClickListener)
    {
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        return itemLongClickListener.onLongClick(v,getAdapterPosition());
    }
}
