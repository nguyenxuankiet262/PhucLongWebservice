package com.phuclongappv2.xk.phuclongappver2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class ActivityStart extends AppCompatActivity {
    private ImageView logoView;
    private Animation anim_alpha;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mService = Common.getAPI();
        checkCurrentUser();
        logoView = (ImageView) findViewById(R.id.logo_Image);
        anim_alpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        anim_alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadStoreList();
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
                                    Common.CurrentUser = response.body();
                                    updateTokenToServer();
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
