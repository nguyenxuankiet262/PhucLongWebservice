package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.phuclongappv2.xk.phuclongappver2.Adapter.CartAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Interface.RecyclerItemTouchHelperListener;
import com.phuclongappv2.xk.phuclongappver2.Model.MyResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.Notification;
import com.phuclongappv2.xk.phuclongappver2.Model.Order;
import com.phuclongappv2.xk.phuclongappver2.Model.Sender;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IFCMService;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.Utils.RecyclerItemTouchHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.widget.FButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    Toolbar toolbar;
    RelativeLayout relativeLayout, existLayout, emptyLayout;
    RecyclerView cartList;
    FButton placeButton;
    TextView total;
    CartAdapter cartAdapter;
    List<Cart> local_listcart = new ArrayList<>();
    FrameLayout details, payment, comment;
    FButton next_details, next_payment, back_payment, next_comment, back_comment;
    MaterialEditText name_detais, address_details, phone_details;
    RadioGroup radioGroup;
    EditText comment_text;
    ArrayAdapter<String> storeAdapter;
    MaterialSpinner materialSpinner;
    int vitri = 0;
    IFCMService apiService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IPhucLongAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        relativeLayout = findViewById(R.id.cart_layout);
        existLayout = findViewById(R.id.cart_exist_layout);
        emptyLayout = findViewById(R.id.empty_cart_layout);

        cartList = findViewById(R.id.cart_list);
        placeButton = findViewById(R.id.order_button);
        total = findViewById(R.id.total_cart);

        cartList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartList.setHasFixedSize(true);

        toolbar = findViewById(R.id.tool_bar_cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(cartList);

        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.cartRepository.countCartItem() != 0) {
                    final Order order = new Order();
                    String[] descriptionData = {"Details", "Payment", "Comment"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCart.this);
                    final View itemView = LayoutInflater.from(ActivityCart.this).inflate(R.layout.popup_submit_place_order, null);
                    final StateProgressBar stateProgressBar = itemView.findViewById(R.id.state_process_bar);
                    stateProgressBar.setStateDescriptionData(descriptionData);
                    details = itemView.findViewById(R.id.dialog_details);
                    payment = itemView.findViewById(R.id.dialog_payment);
                    comment = itemView.findViewById(R.id.dialog_comment);
                    //Init location address
                    materialSpinner = itemView.findViewById(R.id.spinner_address);
                    final List<String> addressList = new ArrayList<>();
                    for(int i = 0; i < Common.CurrentStore.size(); i++){
                        addressList.add(Common.CurrentStore.get(i).getAddress());
                    }
                    storeAdapter = new ArrayAdapter<String>(ActivityCart.this,R.layout.support_simple_spinner_dropdown_item,addressList);
                    materialSpinner.setText(addressList.get(0));
                    materialSpinner.setAdapter(storeAdapter);

                    materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                            vitri = position;
                        }
                    });
                    payment.setVisibility(View.GONE);
                    comment.setVisibility(View.GONE);

                    //Ánh xạ
                    name_detais = itemView.findViewById(R.id.name_dialog);
                    address_details = itemView.findViewById(R.id.address_dialog);
                    phone_details = itemView.findViewById(R.id.phone_dialog);
                    radioGroup = itemView.findViewById(R.id.radio_group);
                    comment_text = itemView.findViewById(R.id.comment_text);

                    next_details = details.findViewById(R.id.dialog_next_details_btn);
                    back_payment = payment.findViewById(R.id.dialog_back_payment_btn);
                    next_payment = payment.findViewById(R.id.dialog_next_payment_btn);
                    back_comment = comment.findViewById(R.id.dialog_back_comment_btn);
                    next_comment = comment.findViewById(R.id.dialog_next_comment_btn);

                    //Init Details

                    name_detais.setText(Common.CurrentUser.getName());
                    ;

                    if (!Common.CurrentUser.getAddress().equals("empty")) {
                        address_details.setText(Common.CurrentUser.getAddress());
                    }
                    if (!Common.CurrentUser.getPhone().equals("empty")) {
                        phone_details.setText(Common.CurrentUser.getPhone());
                    }

                    //Check Button onClick

                    next_details.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(name_detais.getText().toString()) && !TextUtils.isEmpty(address_details.getText().toString()) && !TextUtils.isEmpty(phone_details.getText().toString())) {
                                order.setStoreID(vitri);
                                order.setName(name_detais.getText().toString());
                                order.setAddress(address_details.getText().toString());
                                order.setPhone(phone_details.getText().toString());
                                details.setVisibility(View.GONE);
                                payment.setVisibility(View.VISIBLE);
                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                            } else {
                                Toast.makeText(ActivityCart.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    back_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payment.setVisibility(View.GONE);
                            details.setVisibility(View.VISIBLE);
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                        }
                    });
                    next_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int radioID = radioGroup.getCheckedRadioButtonId();
                            switch (radioID) {
                                case R.id.radio_credit:
                                    order.setPayment("Credit card");
                                    break;
                                case R.id.radio_paypal:
                                    order.setPayment("Paypal card");
                                    break;
                                case R.id.radi_cash_on_delivery:
                                    order.setPayment("Cash on delivery");
                                    break;
                            }
                            payment.setVisibility(View.GONE);
                            comment.setVisibility(View.VISIBLE);
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                        }
                    });

                    back_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            comment.setVisibility(View.GONE);
                            payment.setVisibility(View.VISIBLE);
                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                        }
                    });


                    builder.setView(itemView);
                    final AlertDialog alertDialog = builder.show();

                    next_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mService = Common.getAPI();
                            order.setStoreID(vitri + 1);
                            order.setPrice(total.getText().toString());
                            order.setNote(comment_text.getText().toString());
                            order.setStatus(0);
                            order.setPhone(Common.CurrentUser.getPhone());

                            stateProgressBar.setAllStatesCompleted(true);

                            final ProgressDialog progressDialog;

                            progressDialog = new ProgressDialog(ActivityCart.this);
                            progressDialog.setTitle("Ordering...");
                            progressDialog.setMessage("Please wait for a minute!");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            String timeorder = String.valueOf(System.currentTimeMillis());
                            order.setTimeorder(timeorder);

                            compositeDisposable.add(Common.cartRepository.getCartItems()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe(new Consumer<List<Cart>>() {
                                        @Override
                                        public void accept(List<Cart> carts) throws Exception {
                                            if (carts.size() != 0) {
                                                String drinkdetail = new Gson().toJson(carts);
                                                order.setDrinkdetail(drinkdetail);
                                                mService.insertOrder(order.getAddress(), order.getName(), order.getNote(), order.getPayment(), order.getPhone(),
                                                        order.getPrice(), order.getTimeorder(), order.getDrinkdetail(), order.getStatus(), order.getStoreID())
                                                        .enqueue(new Callback<String>() {
                                                            @Override
                                                            public void onResponse(Call<String> call, final Response<String> response1) {
                                                                mService.updateHistory(Common.CurrentUser.getPhone(),1)
                                                                        .enqueue(new Callback<User>() {
                                                                            @Override
                                                                            public void onResponse(Call<User> call, Response<User> response2) {
                                                                                alertDialog.dismiss();
                                                                                progressDialog.dismiss();
                                                                                sendNotification(order.getTimeorder());
                                                                            }

                                                                            @Override
                                                                            public void onFailure(Call<User> call, Throwable t) {

                                                                            }
                                                                        });
                                                            }

                                                            @Override
                                                            public void onFailure(Call<String> call, Throwable t) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(ActivityCart.this, "Lỗi bất ngờ!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }
                                    })
                            );
                        }
                    });
                } else {

                }
            }
        });
        loadCartItem();
    }

    private void sendNotification(String body) {
        apiService = Common.getFCMService();
        Notification notification = new Notification("Có order mới #"+ body,"New Order");
        Sender content = new Sender(Common.CurrentToken.getToken(),notification);
        apiService.sendNoti(content).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.body().success == 1){
                    total.setText("0 VNĐ");
                    Toast.makeText(ActivityCart.this, "Đặt món thành công! Xin cám ơn quý khách", Toast.LENGTH_SHORT).show();
                    Common.cartRepository.emptyCart();
                    Common.CurrentUser.setNoti_history(1);
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }


    private void loadCartItem() {
        compositeDisposable.add(Common.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        if (Common.cartRepository.countCartItem() == 0) {
                            emptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            existLayout.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                            displayCart(carts);
                        }
                    }
                })
        );

    }

    private void displayCart(List<Cart> carts) {
        local_listcart = carts;
        cartAdapter = new CartAdapter(this, carts, total);
        cartList.setAdapter(cartAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItem();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecyclerView.ViewHolder) {
            String name = local_listcart.get(viewHolder.getAdapterPosition()).cName;

            final Cart deleteItem = local_listcart.get(viewHolder.getAdapterPosition());
            final int deleteItemIndex = viewHolder.getAdapterPosition();

            cartAdapter.removeCart(deleteItemIndex);

            cartAdapter.notifyDataSetChanged();

            Common.cartRepository.deleteCartItem(deleteItem);

            Snackbar snackbar = Snackbar.make(relativeLayout, new StringBuilder(name).append(" đã được xóa khỏi giỏ hàng").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreCart(deleteItem, deleteItemIndex);
                    Common.cartRepository.insertCart(deleteItem);
                    cartAdapter.notifyDataSetChanged();
                    existLayout.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            if (Common.cartRepository.countCartItem() == 0) {
                total.setText("0 VNĐ");
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
