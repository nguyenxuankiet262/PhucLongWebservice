package com.phuclongappv2.xk.phuclongappver2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityStart extends AppCompatActivity {
    private ImageView logoView;
    private Animation anim_alpha;

    IPhucLongAPI mService;

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
                Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logoView.startAnimation(anim_alpha);
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
}
