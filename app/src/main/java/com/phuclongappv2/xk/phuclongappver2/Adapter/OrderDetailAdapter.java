package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.OrderDetailViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailViewHolder> {

    Context context;
    List<Cart> cartList;

    public OrderDetailAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_drink_detail,parent,false);
        return new OrderDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        if(cartList.get(position).cStatus.equals("cold")) {
            Picasso.with(context).load(cartList.get(position).cImageCold).into(holder.imageView_history);
        }
        else if(cartList.get(position).cStatus.equals("hot"))
        {
            Picasso.with(context).load(cartList.get(position).cImageHot).into(holder.imageView_history);
        }
        else {
            holder.imageView_history.setImageResource(R.drawable.thumb_default);
        }
        holder.money_drink.setText(Common.ConvertIntToMoney(cartList.get(position).cPrice));
        holder.name_drink_history.setText(cartList.get(position).cName);
        holder.quanity_drink.setText(String.valueOf(cartList.get(position).cQuanity));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
