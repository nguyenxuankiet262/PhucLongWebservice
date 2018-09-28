package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.RecentDrinkViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecentDrinkAdapter extends RecyclerView.Adapter<RecentDrinkViewHolder> {

    Context context;
    List<SuggestDrink> drinkList;
    String status = "empty";
    ImageView image_banner, image_cold, image_hot;
    TextView drinkname, drinkprice;
    FloatingActionButton cart_btn, rating_btn;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    int price;

    public RecentDrinkAdapter(Context context, List<SuggestDrink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }
    @NonNull
    @Override
    public RecentDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_suggest_drink_layout, parent, false);
        return new RecentDrinkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentDrinkViewHolder holder, final int position) {
        price = drinkList.get(position).dPrice;
        if (!drinkList.get(position).dImageCold.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drinkList.get(position).dImageCold).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_drink, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drinkList.get(position).dImageCold).into(holder.image_drink);
                        }
                    });
            status = "cold";
        } else if (!drinkList.get(position).dImageHot.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drinkList.get(position).dImageHot).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_drink, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drinkList.get(position).dImageHot).into(holder.image_drink);
                        }
                    });
            status = "hot";
        } else {
            holder.image_drink.setImageResource(R.drawable.thumb_default);
        }

        holder.name_drink.setText(drinkList.get(position).dName);
        holder.image_drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(context).inflate(R.layout.popup_drink_detail, null);
                //Ánh xạ

                rating_btn = itemView.findViewById(R.id.btn_rating);
                ratingBar = itemView.findViewById(R.id.rating_bar);
                image_banner = itemView.findViewById(R.id.image_detail_drink);
                image_cold = itemView.findViewById(R.id.cold_drink_image);
                image_hot = itemView.findViewById(R.id.hot_drink_image);

                drinkname = itemView.findViewById(R.id.drink_name);
                drinkprice = itemView.findViewById(R.id.drink_price);

                cart_btn = itemView.findViewById(R.id.btn_cart);
                numberButton = itemView.findViewById(R.id.elegant_btn);
                loadDrinkDetail(drinkList.get(position));

                builder.setView(itemView);
                final AlertDialog alertDialog = builder.show();
                //Click rating button
                rating_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        //showDialogRating();
                    }
                });
                cart_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.cartRepository.isCart(Integer.parseInt(drinkList.get(position).dId)) != 1) {
                            alertDialog.dismiss();
                            //Create DB
                            Cart cartItem = new Cart();
                            if(Common.CurrentUser != null) {
                                cartItem.uId = Common.CurrentUser.getPhone();
                            }
                            cartItem.cId = Integer.parseInt(drinkList.get(position).dId);
                            cartItem.cName = drinkList.get(position).dName;
                            cartItem.cQuanity = Integer.parseInt(numberButton.getNumber());
                            cartItem.cPrice = price;
                            cartItem.cStatus = status;
                            cartItem.cImageCold = drinkList.get(position).dImageCold;
                            cartItem.cImageHot = drinkList.get(position).dImageHot;
                            cartItem.cPriceItem = drinkList.get(position).dPrice;
                            //Add to DB
                            Common.cartRepository.insertCart(cartItem);
                            Toast.makeText(context, "Đã thêm " + numberButton.getNumber() + " " + drinkList.get(position).dName + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            //((ActivityMain) context).updateNotificationHomeIcon();
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, "Sản phẩm đã có trong giỏ hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void loadDrinkDetail(final SuggestDrink drink) {
        if (!drink.dName.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drink.dImageCold).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(image_banner, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drink.dImageCold).into(image_banner);
                        }
                    });
            status = "cold";
        } else if (!drink.dImageHot.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drink.dImageHot).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(image_banner, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drink.dImageHot).into(image_banner);
                        }
                    });
            status = "hot";
        } else {
            image_banner.setImageResource(R.drawable.thumb_default);
            status = "empty";
        }
        drinkname.setText(drink.dName.toString());
        drinkprice.setText(Common.ConvertIntToMoney(drink.dPrice));
        if (drink.dImageCold.equals("empty")) {
            image_cold.setVisibility(View.INVISIBLE);
        }
        if (drink.dImageHot.equals("empty")) {
            image_hot.setVisibility(View.INVISIBLE);
        }
        if (drink.dImageHot.equals("empty") && drink.dImageCold.equals("empty")) {
            image_hot.setVisibility(View.INVISIBLE);
            image_cold.setVisibility(View.INVISIBLE);
        }
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                price = drink.dPrice * Integer.parseInt(numberButton.getNumber());
                //Toast.makeText(context,price,Toast.LENGTH_SHORT).show();
                drinkprice.setText(NumberFormat.getNumberInstance(Locale.US).format(price) + " VNĐ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
