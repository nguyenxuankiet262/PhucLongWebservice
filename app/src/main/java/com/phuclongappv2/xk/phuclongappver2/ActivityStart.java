package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.phuclongappv2.xk.phuclongappver2.Model.Store;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.phuclongappv2.xk.phuclongappver2.Utils.Common.isConnectedToInternet;

public class ActivityStart extends AppCompatActivity {
    private ImageView logoView;
    private Animation anim_alpha;
    private SwipeRefreshLayout swipeRefreshLayout;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mService = Common.getAPI();
        logoView = (ImageView) findViewById(R.id.logo_Image);
        anim_alpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        swipeRefreshLayout = findViewById(R.id.swipe_layout_start);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, android.R.color.holo_green_dark, android.R.color.holo_blue_dark , android.R.color.holo_orange_dark);
        anim_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isConnectedToInternet(getBaseContext())) {
                    checkCurrentUser();
                }
                else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityStart.this);
                    View itemView = LayoutInflater.from(ActivityStart.this).inflate(R.layout.popup_no_internet_layout, null);
                    Button btn_try_again = itemView.findViewById(R.id.btn_try_again);
                    ImageView btn_close = itemView.findViewById(R.id.btn_close);
                    builder.setView(itemView);
                    final AlertDialog alertDialog = builder.show();
                    btn_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    btn_try_again.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isConnectedToInternet(ActivityStart.this)){
                                checkCurrentUser();
                                alertDialog.dismiss();
                            }
                            else{
                                Toast.makeText(ActivityStart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            if(isConnectedToInternet(ActivityStart.this)){
                                checkCurrentUser();
                                alertDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            else{
                                Toast.makeText(ActivityStart.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoView.startAnimation(anim_alpha);
    }

    private void loadStoreList() {
        compositeDisposable.add(mService.getLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Store>>() {
                    @Override
                    public void accept(List<Store> stores) throws Exception {
                        Common.CurrentStore = stores;
                        Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
                        startActivity(intent);
                        finish();
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void checkCurrentUser() {
        if(AccountKit.getCurrentAccessToken() != null){
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    mService.getUser(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            mService.getUser(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if(response.body().getActive() == 1) {
                                        Common.CurrentUser = response.body();
                                        updateTokenToServer();
                                        loadStoreList();
                                    }
                                    else{
                                        Common.CurrentUser = null;
                                        AccountKit.logOut();
                                        loadStoreList();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(ActivityStart.this,"Lỗi bất ngờ!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
        else{
            loadStoreList();
        }
    }

    private void updateTokenToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                IPhucLongAPI mSerVice = Common.getAPI();
                mSerVice.insertToken(Common.CurrentUser.getPhone(), instanceIdResult.getToken(), 0)
                        .enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(Call<Token> call, Response<Token> response) {
                                Common.CurrentToken = response.body();
                            }

                            @Override
                            public void onFailure(Call<Token> call, Throwable t) {

                            }
                        });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityStart.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
