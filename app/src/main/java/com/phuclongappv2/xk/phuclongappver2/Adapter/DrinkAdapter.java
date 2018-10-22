package com.phuclongappv2.xk.phuclongappver2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
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

import com.facebook.accountkit.ui.LoginType;
import com.phuclongappv2.xk.phuclongappver2.ActivityMain;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;
import com.phuclongappv2.xk.phuclongappver2.FragmentMore;
import com.phuclongappv2.xk.phuclongappver2.Interface.ItemClickListener;
import com.phuclongappv2.xk.phuclongappver2.Model.AverageRate;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Model.Rating;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.R;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
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

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkViewHolder> {

    private Context context;
    private List<Drink> drinkList;
    private ImageView image_banner, image_cold, image_hot;
    private TextView drinkname, drinkprice, average_rate;
    private FloatingActionButton cart_btn, rating_btn;
    private ElegantNumberButton numberButton;
    private RatingBar ratingBar;
    private int price;
    private String status = "empty";
    private FragmentActivity drinkactivity;

    CommentAdapter adapter;
    RecyclerView listCmt;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        mService = Common.getAPI();
        //Check click in searchactivity to add suggestdrink database

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
        if(Common.cartRepository.isCart(drinkList.get(position).getID()) != 1) {
            holder.cart_btn.setImageResource(R.drawable.ic_add_shopping_cart_green_24dp);
        }
        else{
            holder.cart_btn.setImageResource(R.drawable.ic_shopping_cart_green_24dp);
        }
        if (Common.favoriteRepository.isFavorite(drinkList.get(position).getID()) == 1) {
            holder.fav_btn.setImageResource(R.drawable.ic_favorite_green_24dp);
        } else {
            holder.fav_btn.setImageResource(R.drawable.ic_favorite_border_green_24dp);
        }
        //Khởi tạo price
        price = drinkList.get(position).getPrice();
        holder.price_drink.setText(Common.ConvertIntToMoney(drinkList.get(position).getPrice()));
        holder.name_drink.setText(drinkList.get(position).getName());

        holder.fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.favoriteRepository.isFavorite(drinkList.get(position).getID()) != 1) {
                    AddOrRemoveFavorite(drinkList.get(position), true);
                    holder.fav_btn.setImageResource(R.drawable.ic_favorite_green_24dp);
                } else {
                    AddOrRemoveFavorite(drinkList.get(position), false);
                    holder.fav_btn.setImageResource(R.drawable.ic_favorite_border_green_24dp);
                }

            }
        });
        holder.share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.CurrentUser != null) {

                }
                else{
                    if(Common.checkInSearchActivity == 0) {
                        ((ActivityMain) context).setLoginPage();
                    }
                    else{
                        Toast.makeText(context,"Bạn cần đăng nhập để thực hiện chức năng này!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View v, final int position) {
                Common.drinkID = drinkList.get(position).getID();
                if(Common.checkInSearchActivity == 1){
                    if (Common.suggestDrinkRepository.isSuggestDrink(drinkList.get(position).getID()) != 1){
                        AddToSuggestDrink(drinkList.get(position));
                    }
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(context).inflate(R.layout.popup_drink_detail, null);
                //Ánh xạ

                average_rate = itemView.findViewById(R.id.average_score);
                rating_btn = itemView.findViewById(R.id.btn_rating);
                ratingBar = itemView.findViewById(R.id.rating_bar);
                image_banner = itemView.findViewById(R.id.image_detail_drink);
                image_cold = itemView.findViewById(R.id.cold_drink_image);
                image_hot = itemView.findViewById(R.id.hot_drink_image);

                drinkname = itemView.findViewById(R.id.drink_name);
                drinkprice = itemView.findViewById(R.id.drink_price);

                cart_btn = itemView.findViewById(R.id.btn_cart);
                numberButton = itemView.findViewById(R.id.elegant_btn);

                listCmt = itemView.findViewById(R.id.list_comment);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);
                listCmt.setLayoutManager(mLayoutManager);

                loadDrinkDetail(drinkList.get(position));
                loadRating(drinkList.get(position).getID());

                loadCmt(drinkList.get(position).getID());

                image_cold.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Picasso picasso = Picasso.with(v.getContext());
                        picasso.setIndicatorsEnabled(false);
                        picasso.load(drinkList.get(position).getImageCold()).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(image_banner, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso picasso = Picasso.with(v.getContext());
                                        picasso.setIndicatorsEnabled(false);
                                        picasso.load(drinkList.get(position).getImageCold()).into(image_banner);
                                    }
                                });
                        status = "cold";
                    }
                });
                image_hot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Picasso picasso = Picasso.with(v.getContext());
                        picasso.setIndicatorsEnabled(false);
                        picasso.load(drinkList.get(position).getImageHot()).networkPolicy(NetworkPolicy.OFFLINE)
                                .into(image_banner, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso picasso = Picasso.with(v.getContext());
                                        picasso.setIndicatorsEnabled(false);
                                        picasso.load(drinkList.get(position).getImageHot()).into(image_banner);
                                    }
                                });
                        status = "hot";
                    }
                });

                builder.setView(itemView);
                final AlertDialog alertDialog = builder.show();
                //Click rating button
                rating_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.hide();
                        showDialogRating();
                    }
                });
                cart_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Common.cartRepository.isCart(drinkList.get(position).getID()) != 1) {
                            alertDialog.dismiss();
                            //Create DB
                            Cart cartItem = new Cart();
                            if(Common.CurrentUser != null) {
                                cartItem.uId = Common.CurrentUser.getPhone();
                            }
                            cartItem.cId = drinkList.get(position).getID();
                            cartItem.cName = drinkList.get(position).getName();
                            cartItem.cQuanity = Integer.parseInt(numberButton.getNumber());
                            cartItem.cPrice = price;
                            cartItem.cStatus = status;
                            cartItem.cImageCold = drinkList.get(position).getImageCold();
                            cartItem.cImageHot = drinkList.get(position).getImageHot();
                            cartItem.cPriceItem = drinkList.get(position).getPrice();
                            //Add to DB
                            Common.cartRepository.insertCart(cartItem);
                            Toast.makeText(context, "Đã thêm " + numberButton.getNumber() + " " + drinkList.get(position).getName() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            if(Common.checkInSearchActivity == 0) {
                                ((ActivityMain) context).updateNotificationHomeIcon();
                            }
                            holder.cart_btn.setImageResource(R.drawable.ic_shopping_cart_green_24dp);
                        } else {
                            alertDialog.dismiss();
                            Toast.makeText(context, "Sản phẩm đã có trong giỏ hàng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void loadCmt(int id) {
        compositeDisposable.add(mService.getRating(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Rating>>() {
                    @Override
                    public void accept(List<Rating> ratings) throws Exception {
                        displayComment(ratings);
                    }
                })
        );
    }

    private void displayComment(List<Rating> ratings) {
        adapter = new CommentAdapter(context, ratings);
        listCmt.setAdapter(adapter);
    }

    private void loadRating(int id) {
        mService.getAvgRate(id).enqueue(new retrofit2.Callback<AverageRate>() {
            @Override
            public void onResponse(Call<AverageRate> call, Response<AverageRate> response) {
                AverageRate averageRate = response.body();
                if(averageRate.getAvg_rate() != null) {
                    ratingBar.setRating(averageRate.getAvg_rate());
                    average_rate.setText(String.valueOf(averageRate.getAvg_rate()));
                }
            }

            @Override
            public void onFailure(Call<AverageRate> call, Throwable t) {
                Log.d("EEEE", t.getMessage());
            }
        });
    }

    private void showDialogRating() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Chấp nhận")
                .setNegativeButtonText("Hủy")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Normal", "Good", "Very Good"))
                .setDefaultRating(5)
                .setTitle("Rating this drink")
                .setDescription("Please select start and give us your feedback")
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.colorPrimaryDark)
                .setHint("Write somethings ...")
                .setCommentTextColor(R.color.colorWhite)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(drinkactivity)
                .show();
    }

    private void AddToSuggestDrink(Drink drink) {
        if(Common.suggestDrinkRepository.countSuggestDrinkItem() > 9){
            Common.suggestDrinkRepository.deleteSuggestDrinkItem(Common.suggestDrinkRepository.getFirstItems());
        }
        SuggestDrink suggestDrink = new SuggestDrink();
        suggestDrink.dId = drink.getID();
        suggestDrink.dCategoryID = drink.getCategoryID();
        suggestDrink.dImageCold = drink.getImageCold();
        suggestDrink.dImageHot = drink.getImageHot();
        suggestDrink.dName = drink.getName();
        suggestDrink.dPrice = drink.getPrice();
        Common.suggestDrinkRepository.insertSuggestDrink(suggestDrink);
    }

    private void loadDrinkDetail(final Drink drink) {
        if (!drink.getImageCold().equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drink.getImageCold()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(image_banner, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drink.getImageCold()).into(image_banner);
                        }
                    });
            status = "cold";
        } else if (!drink.getImageHot().equals("empty")) {
            Picasso picasso = Picasso.with(context);
            picasso.setIndicatorsEnabled(false);
            picasso.load(drink.getImageHot()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(image_banner, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso picasso = Picasso.with(context);
                            picasso.setIndicatorsEnabled(false);
                            picasso.load(drink.getImageHot()).into(image_banner);
                        }
                    });
            status = "hot";
        } else {
            image_banner.setImageResource(R.drawable.thumb_default);
            status = "empty";
        }
        drinkname.setText(drink.getName().toString());
        drinkprice.setText(Common.ConvertIntToMoney(drink.getPrice()));
        if (drink.getImageCold().equals("empty")) {
            image_cold.setVisibility(View.INVISIBLE);
        }
        if (drink.getImageHot().equals("empty")) {
            image_hot.setVisibility(View.INVISIBLE);
        }
        if (drink.getImageHot().equals("empty") && drink.getImageCold().equals("empty")) {
            image_hot.setVisibility(View.INVISIBLE);
            image_cold.setVisibility(View.INVISIBLE);
        }
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                price = drink.getPrice() * Integer.parseInt(numberButton.getNumber());
                //Toast.makeText(context,price,Toast.LENGTH_SHORT).show();
                drinkprice.setText(NumberFormat.getNumberInstance(Locale.US).format(price) + " VNĐ");
            }
        });
    }

    private void AddOrRemoveFavorite(Drink drink, boolean isAdd) {
        Favorite favorite = new Favorite();
        favorite.fId = drink.getID();
        favorite.fName = drink.getName();
        favorite.fImageCold = drink.getImageCold();
        favorite.fImageHot = drink.getImageHot();
        favorite.fPrice = drink.getPrice();
        favorite.fMenu = String.valueOf(drink.getCategoryID());
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
