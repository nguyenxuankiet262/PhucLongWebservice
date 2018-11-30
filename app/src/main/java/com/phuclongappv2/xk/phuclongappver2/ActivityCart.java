package com.phuclongappv2.xk.phuclongappver2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.braintreepayments.api.PaymentMethod;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.phuclongappv2.xk.phuclongappver2.Adapter.CartAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Interface.RecyclerItemTouchHelperListener;
import com.phuclongappv2.xk.phuclongappver2.Model.MyResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.Notification;
import com.phuclongappv2.xk.phuclongappver2.Model.Order;
import com.phuclongappv2.xk.phuclongappver2.Model.Result;
import com.phuclongappv2.xk.phuclongappver2.Model.Sender;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
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

import cz.msebera.android.httpclient.Header;
import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityCart extends AppCompatActivity implements RecyclerItemTouchHelperListener, LocationListener {
    private static final int PAYMENT_REQUEST_CODE = 7777;
    Toolbar toolbar;
    RelativeLayout relativeLayout, existLayout, emptyLayout;
    RecyclerView cartList;
    FButton placeButton;
    TextView total, find_nearest;
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

    SwipeRefreshLayout swipeRefreshLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IPhucLongAPI mService;
    IPhucLongAPI mServiceScalars;

    String token;
    HashMap<String, String> params;
    Order order;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    int index;

    LocationManager lm;
    double latitude;
    double longitude;
    Criteria criteria;
    String bestProvider;
    AlertDialog spotdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mService = Common.getAPI();
        relativeLayout = findViewById(R.id.cart_layout);
        existLayout = findViewById(R.id.cart_exist_layout);
        emptyLayout = findViewById(R.id.empty_cart_layout);

        cartList = findViewById(R.id.cart_list);
        placeButton = findViewById(R.id.order_button);
        total = findViewById(R.id.total_cart);

        swipeRefreshLayout = findViewById(R.id.swipe_layout_cart);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

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
                if (Common.CurrentUser != null) {
                    if(Common.isConnectedToInternet(ActivityCart.this)) {
                        mService.getUser(Common.CurrentUser.getPhone()).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.body().getActive() == 1) {
                                    if (Common.cartRepository.countCartItem() != 0) {
                                        order = new Order();
                                        String[] descriptionData = {"Details", "Comment", "Payment"};
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCart.this);
                                        final View itemView = LayoutInflater.from(ActivityCart.this).inflate(R.layout.popup_submit_place_order, null);
                                        final StateProgressBar stateProgressBar = itemView.findViewById(R.id.state_process_bar);
                                        stateProgressBar.setStateDescriptionData(descriptionData);
                                        details = itemView.findViewById(R.id.dialog_details);
                                        payment = itemView.findViewById(R.id.dialog_payment);
                                        comment = itemView.findViewById(R.id.dialog_comment);
                                        //Init location address
                                        final List<String> addressList = new ArrayList<>();
                                        for (int i = 0; i < Common.CurrentStore.size(); i++) {
                                            addressList.add(Common.CurrentStore.get(i).getAddress());
                                        }
                                        storeAdapter = new ArrayAdapter<String>(ActivityCart.this, R.layout.support_simple_spinner_dropdown_item, addressList);

                                        payment.setVisibility(View.GONE);
                                        comment.setVisibility(View.GONE);

                                        //Ánh xạ
                                        name_detais = itemView.findViewById(R.id.name_dialog);
                                        address_details = itemView.findViewById(R.id.address_dialog);
                                        phone_details = itemView.findViewById(R.id.phone_dialog);
                                        radioGroup = itemView.findViewById(R.id.radio_group);
                                        comment_text = itemView.findViewById(R.id.comment_text);

                                        next_details = details.findViewById(R.id.dialog_next_details_btn);
                                        materialSpinner = details.findViewById(R.id.spinner_address);
                                        find_nearest = details.findViewById(R.id.btn_find_nearest_store);

                                        back_payment = payment.findViewById(R.id.dialog_back_payment_btn);
                                        next_payment = payment.findViewById(R.id.dialog_next_payment_btn);

                                        back_comment = comment.findViewById(R.id.dialog_back_comment_btn);
                                        next_comment = comment.findViewById(R.id.dialog_next_comment_btn);

                                        //Init Details
                                        find_nearest.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                spotdialog = new SpotsDialog.Builder().setContext(ActivityCart.this).build();
                                                spotdialog.setMessage("Please waiting...");
                                                spotdialog.show();
                                                MakeFindNearest();
                                            }
                                        });
                                        materialSpinner.setText(addressList.get(0));
                                        materialSpinner.setAdapter(storeAdapter);

                                        materialSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                                vitri = position;
                                            }
                                        });

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
                                                    comment.setVisibility(View.VISIBLE);
                                                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                                } else {
                                                    Toast.makeText(ActivityCart.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        back_comment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                comment.setVisibility(View.GONE);
                                                details.setVisibility(View.VISIBLE);
                                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                                            }
                                        });


                                        builder.setView(itemView);
                                        alertDialog = builder.show();

                                        next_comment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                order.setNote(comment_text.getText().toString());
                                                payment.setVisibility(View.VISIBLE);
                                                comment.setVisibility(View.GONE);
                                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                                            }
                                        });
                                        back_payment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                payment.setVisibility(View.GONE);
                                                comment.setVisibility(View.VISIBLE);
                                                stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                                            }
                                        });
                                        next_payment.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                int radioID = radioGroup.getCheckedRadioButtonId();
                                                order.setStoreID(vitri + 1);
                                                order.setPrice(total.getText().toString());
                                                order.setStatus(0);
                                                order.setPhone(Common.CurrentUser.getPhone());
                                                String timeorder = String.valueOf(System.currentTimeMillis());
                                                order.setTimeorder(timeorder);
                                                stateProgressBar.setAllStatesCompleted(true);
                                                //Toast.makeText(ActivityCart.this,token,Toast.LENGTH_SHORT).show();
                                                switch (radioID) {
                                                    case R.id.radio_credit:
                                                        mServiceScalars = Common.getScalarsAPI();
                                                        order.setPayment("Credit card");
                                                        DropInRequest dropInRequest = new DropInRequest().clientToken(token);
                                                        dropInRequest.disablePayPal();
                                                        startActivityForResult(dropInRequest.getIntent(ActivityCart.this), PAYMENT_REQUEST_CODE);
                                                        break;
                                                    case R.id.radi_cash_on_delivery:
                                                        order.setPayment("Cash on delivery");
                                                        InsertOrder();
                                                        break;
                                                }
                                            }
                                        });

                                    } else {

                                    }
                                } else {
                                    Toast.makeText(ActivityCart.this, "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });
                    }
                    else{
                        Toast.makeText(ActivityCart.this,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ActivityCart.this,"Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadCartItem();
                    loadToken();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityCart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    loadCartItem();
                    loadToken();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivityCart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void MakeFindNearest() {
        if(ActivityCompat.checkSelfPermission(ActivityCart.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivityCart.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(Common.isLocationEnabled(this)) {
                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(lm.getBestProvider(criteria, true)).toString();
                Location location = lm.getLastKnownLocation(bestProvider);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    lm.requestLocationUpdates(bestProvider, 1000, 0, this);
                }
                index = 0;
                double min = meterDistanceBetweenPoints((float) latitude, (float) longitude
                        , Float.parseFloat(Common.CurrentStore.get(0).getLat())
                        , Float.parseFloat(Common.CurrentStore.get(0).getLng())
                );
                for (int i = 1; i < Common.CurrentStore.size(); i++) {
                    double meter = meterDistanceBetweenPoints((float) latitude, (float) longitude
                            , Float.parseFloat(Common.CurrentStore.get(i).getLat())
                            , Float.parseFloat(Common.CurrentStore.get(i).getLng())
                    );
                    if (meter < min) {
                        index = i;
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        spotdialog.dismiss();
                        materialSpinner.setSelectedIndex(index);
                        vitri = index;
                        Toast.makeText(ActivityCart.this, "Tìm thành công!", Toast.LENGTH_SHORT).show();
                    }
                }, 2000);
            }
            else{

            }
        }
        else{
            ActivityCompat.requestPermissions(ActivityCart.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                MakeFindNearest();
            }
            else{

            }
        }
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAYMENT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String strNonce = nonce.getNonce();
                String[] amounts = total.getText().toString().split(",");
                float value = Float.parseFloat(amounts[0]) / 22;
                String amount = String.valueOf((float) Math.round(value * 100) / 100);
                params = new HashMap<>();
                params.put("amount", amount);
                params.put("nonce", strNonce);
                sendPayment();
            }
            else if (resultCode == RESULT_CANCELED){
            }
            else{
                Exception er = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.e("EEE",er.getMessage());
            }
        }
    }

    private void sendPayment() {
        mServiceScalars.payment(params.get("nonce"), params.get("amount")).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().toString().contains("Successful")){
                    InsertOrder();
                }else{
                    Toast.makeText(ActivityCart.this,"Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("EEES", t.getMessage());
            }
        });
    }

    private void InsertOrder() {
        progressDialog = new ProgressDialog(ActivityCart.this);
        progressDialog.setTitle("Ordering...");
        progressDialog.setMessage("Please wait for a minute!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        if (Common.isConnectedToInternet(getBaseContext())) {
            List<Cart> carts = Common.cartRepository.getCartItems();
            if (carts.size() != 0) {
                String drinkdetail = new Gson().toJson(carts);
                order.setDrinkdetail(drinkdetail);
                mService.insertOrder(order.getAddress(), order.getName(), order.getNote(), order.getPayment(), order.getPhone(),
                        order.getPrice(), order.getTimeorder(), order.getDrinkdetail(), order.getStatus(), order.getStoreID())
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, final Response<String> response1) {
                                mService.updateHistory(Common.CurrentUser.getPhone(), 1)
                                        .enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response2) {
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
        } else {
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), "Kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadToken() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Common.API_TOKEN_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ActivityCart.this, throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                token =responseString;
            }
        });
    }

    private void sendNotification(String body) {
        apiService = Common.getFCMService();
        final Notification notification = new Notification("Có order mới #"+ body,"New Order");
        mService.getToken("admin",1).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Token token = response.body();
                Sender content = new Sender(token.getToken(),notification);
                apiService.sendNoti(content).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if(response.body().success == 1){
                            alertDialog.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(ActivityCart.this, "Đặt món thành công! Xin cám ơn quý khách", Toast.LENGTH_SHORT).show();
                            Common.cartRepository.emptyCart();
                            Common.CurrentUser.setNoti_history(1);
                            total.setText("0 VNĐ");
                            loadCartItem();
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

            }
        });

    }


    private void loadCartItem() {
        List<Cart> carts = Common.cartRepository.getCartItems();
        if (Common.cartRepository.countCartItem() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            existLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            displayCart(carts);
        }
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

    @Override
    public void onLocationChanged(Location location) {
        lm.removeUpdates(this);

        //open the map:
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        index = 0;
        double min = meterDistanceBetweenPoints((float) latitude, (float) longitude
                , Float.parseFloat(Common.CurrentStore.get(0).getLat())
                , Float.parseFloat(Common.CurrentStore.get(0).getLng())
        );
        for (int i = 1; i < Common.CurrentStore.size(); i++) {
            double meter = meterDistanceBetweenPoints((float) latitude, (float) longitude
                    , Float.parseFloat(Common.CurrentStore.get(i).getLat())
                    , Float.parseFloat(Common.CurrentStore.get(i).getLng())
            );
            if (meter < min) {
                index = i;
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spotdialog.dismiss();
                materialSpinner.setSelectedIndex(index);
                vitri = index;
                Toast.makeText(ActivityCart.this, "Tìm thành công!", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
