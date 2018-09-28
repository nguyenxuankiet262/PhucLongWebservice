package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentDrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public CircleImageView image_drink;
    public TextView name_drink;

    ItemClickListener itemClickListener;
    public RecentDrinkViewHolder(View itemView) {
        super(itemView);
        image_drink = itemView.findViewById(R.id.recent_image_drink);
        name_drink = itemView.findViewById(R.id.recent_name_drink);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}
