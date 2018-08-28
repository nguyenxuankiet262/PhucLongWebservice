package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView image_product, fav_btn, share_btn;
    public TextView name_drink, price_drink;

    ItemClickListener itemClickListener;
    public DrinkViewHolder(View itemView) {
        super(itemView);
        share_btn = itemView.findViewById(R.id.share_btn);
        fav_btn = itemView.findViewById(R.id.fav_btn);
        image_product = itemView.findViewById(R.id.image_drink);
        name_drink = itemView.findViewById(R.id.name_drink);
        price_drink = itemView.findViewById(R.id.price_drink);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
