package com.phuclongappv2.xk.phuclongappver2.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView img_product;
    public TextView name_product;
    public ItemClickListener itemClickListener;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        img_product = itemView.findViewById(R.id.image_product);
        name_product = itemView.findViewById(R.id.text_product);
        img_product.setOnClickListener(this);
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
