package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phuclongappv2.xk.phuclongappver2.R;

public class CartViewHolder extends RecyclerView.ViewHolder{
    public ImageView image_cart;
    public TextView status_hot, status_cold, name_cart, price_cart;
    public ElegantNumberButton quanity_cart;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public CartViewHolder(View itemView) {
        super(itemView);

        view_background = itemView.findViewById(R.id.view_background);
        view_foreground = itemView.findViewById(R.id.view_foreground);
        image_cart = itemView.findViewById(R.id.image_cart);
        status_hot = itemView.findViewById(R.id.status_hot_cart);
        status_cold = itemView.findViewById(R.id.status_cold_cart);
        name_cart = itemView.findViewById(R.id.name_cart);
        price_cart = itemView.findViewById(R.id.price_cart);
        quanity_cart = itemView.findViewById(R.id.quanity_cart);
    }
}
