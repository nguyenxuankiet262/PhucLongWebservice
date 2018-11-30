package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.phuclongappv2.xk.phuclongappver2.ActivityMain;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.FragmentDrink;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemLongClickListener;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.ViewHolder.FavoriteViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {
    Context context;
    List<Favorite> favoriteList;
    ImageView image_cold, image_hot;
    Button accept, cancel;
    ElegantNumberButton elegantNumberButton;
    int price, prices, topping;
    private String textTopping, sugar, ice;
    String status;
    Fragment fragment;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList, Fragment fragment) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_layout, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteViewHolder holder, final int position) {
        //Khởi tạo adapter
        if (!favoriteList.get(position).fImageCold.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(favoriteList.get(position).fImageCold).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_product, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(favoriteList.get(position).fImageCold).into(holder.image_product);
                        }
                    });
        } else if (!favoriteList.get(position).fImageHot.equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(favoriteList.get(position).fImageHot).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.image_product, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(favoriteList.get(position).fImageHot).into(holder.image_product);
                        }
                    });
        } else {
            holder.image_product.setImageResource(R.drawable.thumb_default);
        }
        holder.name_drink.setText(favoriteList.get(position).fName);
        holder.price_drink.setText(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(String.valueOf(favoriteList.get(position).fPrice))) + " VNĐ");
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                showDialogAdd(position);
            }
        }, new ItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position) {
                showDialogDelete(position);
                return true;
            }
        });
    }

    private void showDialogAdd(final int position) {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.popup_favorite_layout);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        image_cold = alertDialog.findViewById(R.id.cold_drink_fav_image);
        image_hot = alertDialog.findViewById(R.id.hot_drink_fav_image);
        if (!favoriteList.get(position).fImageCold.equals("empty")) {
            image_hot.setAlpha(123);
            image_cold.setAlpha(255);
            status = "cold";
        } else if (!favoriteList.get(position).fImageHot.equals("empty")) {
            image_cold.setAlpha(123);
            image_hot.setAlpha(255);
            status = "hot";
        }
        image_cold.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!favoriteList.get(position).fImageCold.equals("empty")) {
                    image_cold.setAlpha(255);
                    image_hot.setAlpha(123);
                    status = "cold";
                } else {
                    Toast.makeText(context, "Không có loại Lạnh cho sản phẩm này!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        image_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favoriteList.get(position).fImageHot.equals("empty")) {
                    image_hot.setAlpha(255);
                    image_cold.setAlpha(123);
                    status = "hot";
                } else {
                    Toast.makeText(context, "Không có loại Nóng cho sản phẩm này!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        price = favoriteList.get(position).fPrice;
        elegantNumberButton = alertDialog.findViewById(R.id.elegant_fav_btn);
        accept = alertDialog.findViewById(R.id.accept_add_cart);
        cancel = alertDialog.findViewById(R.id.cancel_add_cart);
        elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                price = favoriteList.get(position).fPrice * Integer.parseInt(elegantNumberButton.getNumber());
                //Toast.makeText(context,price,Toast.LENGTH_SHORT).show();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topping = 0;
                textTopping = "";
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View itemView = LayoutInflater.from(context).inflate(R.layout.popup_extra_details_drink_layout, null);
                builder.setView(itemView);
                final AlertDialog extraDialog = builder.show();
                Button btn_addtocart = itemView.findViewById(R.id.btn_add_to_cart);
                final ElegantNumberButton extra_elegant = itemView.findViewById(R.id.elegant_extra);
                TextView extra_name = itemView.findViewById(R.id.name_extra);
                final TextView total_price = itemView.findViewById(R.id.total_price);
                ImageView extra_image = itemView.findViewById(R.id.image_extra);
                final RadioGroup sugar_group, ice_group;
                sugar_group = itemView.findViewById(R.id.radio_group_sugar);
                ice_group = itemView.findViewById(R.id.radio_group_ice);
                final CheckBox milk_cream, seeds, white_pearl, black_pearl, red_beans;
                milk_cream = itemView.findViewById(R.id.extra_topping_milk_dream);
                seeds = itemView.findViewById(R.id.extra_topping_seeds);
                white_pearl = itemView.findViewById(R.id.extra_topping_white_pearl);
                black_pearl = itemView.findViewById(R.id.extra_topping_black_pearl);
                red_beans = itemView.findViewById(R.id.extra_topping_red_beans);
                final EditText extra_comment = itemView.findViewById(R.id.comment_extra);

                milk_cream.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked == true){
                            topping += 10000;
                            prices += 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        else{
                            topping -= 10000;
                            prices -= 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                seeds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked == true){
                            topping += 10000;
                            prices += 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        else{
                            topping -= 10000;
                            prices -= 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                white_pearl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked == true){
                            topping += 10000;
                            prices += 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        else{
                            topping -= 10000;
                            prices -= 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                black_pearl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked == true){
                            topping += 10000;
                            prices += 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        else{
                            topping -= 10000;
                            prices -= 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                red_beans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked == true){
                            topping += 10000;
                            prices += 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        else{
                            topping -= 10000;
                            prices -= 10000 * Integer.parseInt(extra_elegant.getNumber());
                        }
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(price) + " VNĐ");
                extra_elegant.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        prices =  (favoriteList.get(position).fPrice + topping) * Integer.parseInt(extra_elegant.getNumber());
                        total_price.setText(NumberFormat.getNumberInstance(Locale.US).format(prices) + " VNĐ");
                    }
                });
                extra_elegant.setNumber(elegantNumberButton.getNumber());
                prices = favoriteList.get(position).fPrice * Integer.parseInt(extra_elegant.getNumber());
                String exName;
                if(status.equals("hot")){
                    exName = favoriteList.get(position).fName + " (HOT)";
                    Picasso.with(context).load(favoriteList.get(position).fImageHot).into(extra_image);
                }
                else{
                    exName = favoriteList.get(position).fName + " (COLD)";
                    Picasso.with(context).load(favoriteList.get(position).fImageCold).into(extra_image);
                }
                extra_name.setText(exName);

                btn_addtocart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Sugar
                        if(sugar_group.getCheckedRadioButtonId() == R.id.extra_sugar_100){
                            sugar = "100%";
                            //Toast.makeText(context, sugar + "", Toast.LENGTH_SHORT).show();
                        }
                        else if(sugar_group.getCheckedRadioButtonId() == R.id.extra_sugar_70){
                            sugar = "70%";
                            //Toast.makeText(context, sugar + "", Toast.LENGTH_SHORT).show();
                        }
                        else if(sugar_group.getCheckedRadioButtonId() == R.id.extra_sugar_50){
                            sugar = "50%";
                            //Toast.makeText(context, sugar + "", Toast.LENGTH_SHORT).show();
                        }
                        else if(sugar_group.getCheckedRadioButtonId() == R.id.extra_sugar_30){
                            sugar = "30%";
                            //Toast.makeText(context, sugar + "", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            sugar = "Free";
                            //Toast.makeText(context, sugar + "", Toast.LENGTH_SHORT).show();
                        }

                        //Ice
                        if(ice_group.getCheckedRadioButtonId() == R.id.extra_ice_100){
                            ice = "100%";
                            //Toast.makeText(context, "a100", Toast.LENGTH_SHORT).show();
                        }
                        else if(ice_group.getCheckedRadioButtonId() == R.id.extra_ice_70){
                            ice = "70%";
                            //Toast.makeText(context, "a70", Toast.LENGTH_SHORT).show();
                        }
                        else if(ice_group.getCheckedRadioButtonId() == R.id.extra_ice_50){
                            ice = "50%";
                            //Toast.makeText(context, "a50", Toast.LENGTH_SHORT).show();
                        }
                        else if(ice_group.getCheckedRadioButtonId() == R.id.extra_ice_30){
                            ice = "30%";
                            //Toast.makeText(context, "a30", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ice = "Free";
                            //Toast.makeText(context, "aFree", Toast.LENGTH_SHORT).show();
                        }

                        if(milk_cream.isChecked()){
                            textTopping += "Milk cream\n";
                        }
                        if(white_pearl.isChecked()){
                            textTopping += "White Pearl\n";
                        }
                        if(black_pearl.isChecked()){
                            textTopping += "Black Pearl\n";
                        }
                        if(red_beans.isChecked()){
                            textTopping += "Red Beans\n";
                        }
                        if(seeds.isChecked()){
                            textTopping += "Seeds\n";
                        }
                        //Toast.makeText(context, textTopping, Toast.LENGTH_SHORT).show();
                        Cart cartItem = new Cart();
                        cartItem.cName = favoriteList.get(position).fName;
                        cartItem.cQuanity = Integer.parseInt(extra_elegant.getNumber());
                        cartItem.cPrice = prices;
                        cartItem.cStatus = status;
                        cartItem.cImageCold = favoriteList.get(position).fImageCold;
                        cartItem.cImageHot = favoriteList.get(position).fImageHot;
                        cartItem.cPriceItem = favoriteList.get(position).fPrice;
                        cartItem.cIce = ice;
                        cartItem.cSugar = sugar;
                        cartItem.cTopping = textTopping;
                        cartItem.cComment = extra_comment.getText().toString();
                        cartItem.cPriceTopping = topping;
                        Common.cartRepository.insertCart(cartItem);
                        ((ActivityMain) context).updateNoti();
                        Toast.makeText(context, "Đã thêm " + extra_elegant.getNumber() + " " + favoriteList.get(position).fName + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        extraDialog.dismiss();
                    }
                });

                //Create DB
                /*Cart cartItem = new Cart();
                cartItem.uId = Common.CurrentUser.getPhone();
                cartItem.cId = favoriteList.get(position).fId;
                cartItem.cName = favoriteList.get(position).fName;
                cartItem.cQuanity = Integer.parseInt(elegantNumberButton.getNumber());
                cartItem.cPrice = price;
                cartItem.cStatus = status;
                cartItem.cImageCold = favoriteList.get(position).fImageCold;
                cartItem.cImageHot = favoriteList.get(position).fImageHot;
                cartItem.cPriceItem = favoriteList.get(position).fPrice;
                //Add to DB
                Common.cartRepository.insertCart(cartItem);
                ((ActivityMain) context).updateNoti();
                Toast.makeText(context, "Đã thêm " + elegantNumberButton.getNumber() + " " + favoriteList.get(position).fName + " vào giỏ hàng", Toast.LENGTH_SHORT).show();*/
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showDialogDelete(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn xóa không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Chấp nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Favorite tempFav = favoriteList.get(index);
                final FragmentDrink drinkFragment = (FragmentDrink)fragment.getActivity().getSupportFragmentManager().findFragmentByTag("DrinkFragment");
                //Delete item from adapter

                final String id = favoriteList.get(index).fMenu;
                removeItem(index);
                //Delete item from Room Database
                Common.favoriteRepository.deleteFavItem(tempFav);
                dialogInterface.dismiss();
                Snackbar snackbar = Snackbar.make(Common.parentFavLayout, new StringBuilder(tempFav.fName).append(" đã được xóa khỏi danh sách Favorites").toString(),
                        Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restore(tempFav, index);
                        Common.favoriteRepository.insertCart(tempFav);
                        if(Common.checkDrinkFragmentOpen == true) {
                            drinkFragment.loadDrink(id);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
                if(Common.checkDrinkFragmentOpen == true) {
                    drinkFragment.loadDrink(id);
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeItem(int index) {
        favoriteList.remove(index);
        notifyItemRemoved(index);
    }

    private void restore(Favorite favorite, int index) {
        favoriteList.add(index, favorite);
        notifyItemInserted(index);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}
