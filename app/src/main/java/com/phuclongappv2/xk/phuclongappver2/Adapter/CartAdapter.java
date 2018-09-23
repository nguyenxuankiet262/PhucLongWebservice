package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    Context context;
    List<Cart> cartList;
    TextView total;

    public CartAdapter(Context context, List<Cart> cartList, TextView total){
        this.context = context;
        this.cartList = cartList;
        this.total = total;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_cart_layout  ,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        //Click vào cart

        //Khởi tạo total
        int sum = 0;
        for (int i = 0; i < cartList.size(); i++) {
            sum += cartList.get(i).cPrice;
        }
        total.setText(NumberFormat.getNumberInstance(Locale.US).format(sum) + " VNĐ");

        //Khởi tạo Image_cold của Cart
        if(cartList.get(position).cStatus.equals("cold")) {
            Picasso.with(context).load(cartList.get(position).cImageCold).into(holder.image_cart);
            holder.status_cold.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.status_cold.setTypeface(null,Typeface.BOLD);

            holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorTextView));
            holder.status_hot.setTypeface(null,Typeface.NORMAL);

        }
        //Khởi tạo Image_hot của Cart
        else if (cartList.get(position).cStatus.equals("hot")) {
            Picasso.with(context).load(cartList.get(position).cImageHot).into(holder.image_cart);
            holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.status_hot.setTypeface(null,Typeface.BOLD);

            holder.status_cold.setTextColor(context.getResources().getColor(R.color.colorTextView));
            holder.status_cold.setTypeface(null,Typeface.NORMAL);

        }
        //Khởi tạo No_Image của Cart
        else {
            holder.image_cart.setImageResource(R.drawable.thumb_default);
        }
        //Khởi tạo tên, giá. status, số lượng
        holder.name_cart.setText(cartList.get(position).cName);
        holder.price_cart.setText(NumberFormat.getNumberInstance(Locale.US).format(cartList.get(position).cPrice) + " VNĐ");
        if(cartList.get(position).cStatus == "hot"){
            holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.status_hot.setTypeface(null,Typeface.BOLD);
        }
        if(cartList.get(position).cStatus == "cold"){
            holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.status_hot.setTypeface(null,Typeface.BOLD);
        }
        holder.quanity_cart.setNumber(String.valueOf(cartList.get(position).cQuanity));

        //Khi số lượng thay đổi
        holder.quanity_cart.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Log.d("TTT","Change");
                int price;
                price = cartList.get(position).cPriceItem * Integer.parseInt(holder.quanity_cart.getNumber());

                Log.d("TTT",NumberFormat.getNumberInstance(Locale.US).format(price) + " VNĐ");

                holder.price_cart.setText(NumberFormat.getNumberInstance(Locale.US).format(price) + " VNĐ");

                Cart cart = cartList.get(position);
                cart.cPrice = price;
                cart.cQuanity = newValue;
                Common.cartRepository.updateCart(cart);
                int sum = 0;
                for (int i = 0; i < cartList.size(); i++) {
                    sum += cartList.get(i).cPrice;
                }
                total.setText(NumberFormat.getNumberInstance(Locale.US).format(sum) + " VNĐ");

            }
        });

        //Click vào chữ Hot
        holder.status_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check có loại nóng hay không
                if(!cartList.get(position).cImageHot.equals("empty")) {
                    Picasso.with(context).load(cartList.get(position).cImageHot).into(holder.image_cart);

                    holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    holder.status_hot.setTypeface(null, Typeface.BOLD);

                    holder.status_cold.setTextColor(context.getResources().getColor(R.color.colorTextView));
                    holder.status_cold.setTypeface(null, Typeface.NORMAL);

                    Cart cart = cartList.get(position);
                    cart.cStatus = "hot";
                    Common.cartRepository.updateCart(cart);
                }
                else{
                    Toast.makeText(context,"Không có loại Hot cho sản phẩm này!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Click vào chữ Cold
        holder.status_cold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check có loại lạnh hay không
                if(!cartList.get(position).cImageCold.equals("empty")) {
                    Picasso.with(context).load(cartList.get(position).cImageCold).into(holder.image_cart);

                    holder.status_cold.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    holder.status_cold.setTypeface(null, Typeface.BOLD);

                    holder.status_hot.setTextColor(context.getResources().getColor(R.color.colorTextView));
                    holder.status_hot.setTypeface(null, Typeface.NORMAL);

                    Cart cart = cartList.get(position);
                    cart.cStatus = "cold";
                    Common.cartRepository.updateCart(cart);
                }
                else{
                    Toast.makeText(context,"Không có loại Cold cho sản phẩm này!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void removeCart(int deleteItemIndex) {
        cartList.remove(deleteItemIndex);
        notifyItemRemoved(deleteItemIndex);
    }

    public void restoreCart(Cart item , int position){
        cartList.add(position,item);
        notifyItemInserted(position);
    }
}
