package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phuclongappv2.xk.phuclongappver2.Adapter.OrderDetailAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemLongClickListener;
import com.phuclongappv2.xk.phuclongappver2.Model.Order;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.OrderViewHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<Order> orderList;
    FrameLayout details, info;
    RecyclerView list_order;
    TextView id_order, time_order, status_order, comment_order, total_order;
    List<Cart> cartList;
    OrderDetailAdapter adapter;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_layout, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.time_order.setText(Common.getTimeAgo(Long.parseLong(orderList.get(position).getTimeorder()), context));
        holder.id_order.setText("#" + orderList.get(position).getTimeorder());
        if (orderList.get(position).getStatus() == 0) {
            holder.status_order.setText("New Order");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOpenStore));
        }
        if (orderList.get(position).getStatus() == 1) {
            holder.status_order.setText("On the way");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOTW));
        }
        if (orderList.get(position).getStatus() == 2) {
            holder.status_order.setText("Success");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorSc));
        }
        if (orderList.get(position).getStatus() == 3) {
            holder.status_order.setText("Delete");
            holder.status_order.setTextColor(ContextCompat.getColor(context, R.color.colorCancel));
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, final int position) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Cart>>(){}.getType();
                List<Cart> cartList = gson.fromJson(orderList.get(position).getDrinkdetail(), type);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View itemView = LayoutInflater.from(context).inflate(R.layout.item_order_detail, null);
                details = itemView.findViewById(R.id.order_details);
                id_order = details.findViewById(R.id.id_order_history);
                time_order = details.findViewById(R.id.time_order_history);
                status_order = details.findViewById(R.id.status_order_history);
                comment_order = details.findViewById(R.id.comment_order_history);
                total_order = details.findViewById(R.id.total_history);

                list_order = details.findViewById(R.id.list_order_history);
                list_order.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                list_order.setHasFixedSize(true);

                adapter = new OrderDetailAdapter(context, cartList);
                list_order.setAdapter(adapter);

                total_order.setText("TỔNG: " + orderList.get(position).getPrice());

                if(TextUtils.isEmpty(orderList.get(position).getNote())){
                    comment_order.setText("Trống!");
                }
                else{
                    comment_order.setText(orderList.get(position).getNote());
                }

                id_order.setText("#" + orderList.get(position).getTimeorder());
                time_order.setText(Common.getTimeAgo(Long.parseLong(orderList.get(position).getTimeorder()), context));
                if (orderList.get(position).getStatus() == 0) {
                    status_order.setText("New Order");
                    status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOpenStore));
                }
                if (orderList.get(position).getStatus()== 1) {
                    status_order.setText("On the way");
                    status_order.setTextColor(ContextCompat.getColor(context, R.color.colorOTW));
                }
                if (orderList.get(position).getStatus()== 2) {
                    status_order.setText("Success");
                    status_order.setTextColor(ContextCompat.getColor(context, R.color.colorSc));
                }
                if (orderList.get(position).getStatus()== 3) {
                    status_order.setText("Delete");
                    status_order.setTextColor(ContextCompat.getColor(context, R.color.colorCancel));
                }
                builder.setView(itemView);
                final AlertDialog alertDialog = builder.show();
            }
        }, new ItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position) {

                Toast.makeText(context, "Long", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
