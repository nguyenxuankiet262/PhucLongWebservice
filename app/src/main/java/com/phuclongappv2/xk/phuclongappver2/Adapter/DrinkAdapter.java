package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.DrinkViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stepstone.apprating.AppRatingDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    Context context;
    List<Drink> drinkList;
    ImageView image_banner, image_cold, image_hot;
    TextView drinkname, drinkprice;
    FloatingActionButton cart_btn, rating_btn;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    int price;
    String status = "empty";
    FragmentActivity drinkactivity;

    RecyclerView listCmt;

    public DrinkAdapter(Context context, List<Drink> drinkList, FragmentActivity drinkactivity) {
        this.context = context;
        this.drinkList = drinkList;
        this.drinkactivity = drinkactivity;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_drink_layout, parent, false);
        return new DrinkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrinkViewHolder holder, final int position) {
        //Khởi tạo image
        if (!drinkList.get(position).getImageCold().equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drinkList.get(position).getImageCold()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_product, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drinkList.get(position).getImageCold()).into(holder.image_product);
                        }
                    });
            status = "cold";
        } else if (!drinkList.get(position).getImageHot().equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drinkList.get(position).getImageHot()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_product, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drinkList.get(position).getImageHot()).into(holder.image_product);
                        }
                    });
            status = "hot";
        } else {
            holder.image_product.setImageResource(R.drawable.thumb_default);
        }
        //Khởi tạo price
        holder.price_drink.setText(Common.ConvertIntToMoney(drinkList.get(position).getPrice()));
        holder.name_drink.setText(drinkList.get(position).getName());

        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.CurrentUser != null) {
                    if (Common.favoriteRepository.isFavorite(Integer.parseInt(drinkList.get(position).getID()), Common.CurrentUser.getId()) != 1) {
                        AddOrRemoveFavorite(drinkList.get(position), true);
                        holder.fav_btn.setImageResource(R.drawable.ic_favorite_green_24dp);
                    } else {
                        AddOrRemoveFavorite(drinkList.get(position), false);
                        holder.fav_btn.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                    }
                }
                else{
                    ShowLoginPopup();
                }
            }
        });
    }

    private void ShowLoginPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn đăng nhập không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Chấp nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void AddOrRemoveFavorite(Drink drink, boolean isAdd) {
        Favorite favorite = new Favorite();
        favorite.uId = Common.CurrentUser.getId();
        favorite.fId = Integer.parseInt(drink.getID());
        favorite.fName = drink.getName();
        favorite.fImageCold = drink.getImageCold();
        favorite.fImageHot = drink.getImageHot();
        favorite.fPrice = drink.getPrice();
        favorite.fMenu = drink.getCategoryID();
        if (isAdd) {
            Common.favoriteRepository.insertCart(favorite);
        } else {
            Common.favoriteRepository.deleteFavItem(favorite);
        }
    }


    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
