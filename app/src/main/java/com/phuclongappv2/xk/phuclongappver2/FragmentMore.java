package com.phuclongappv2.xk.phuclongappver2;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.phuclongappv2.xk.phuclongappver2.Model.CheckUserResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.User;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMore extends Fragment {

    FButton btn_login;
    RelativeLayout login_layout;

    private static final int REQUEST_CODE = 1000;

    IPhucLongAPI mService;


    public FragmentMore() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mService = Common.getAPI();

        login_layout = view.findViewById(R.id.login_layout);
        if(Common.CurrentUser != null){
            login_layout.setVisibility(View.GONE);
        }
        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.PHONE);
            }
        });
        if(Common.CurrentUser != null){
            login_layout.setVisibility(View.GONE);
        }
        else{
            login_layout.setVisibility(View.VISIBLE);
        }
    }

    public void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if(result.getError() != null){
                Toast.makeText(getActivity(),""+result.getError().getErrorType().getMessage(),Toast.LENGTH_LONG).show();
            }
            else if(result.wasCancelled()){

            }
            else{
                if(result.getAccessToken() != null){
                    final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
                    alertDialog.setMessage("Please waiting...");
                    alertDialog.show();

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.registerUser(account.getPhoneNumber().toString(),"","","").enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    mService.getUser(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            alertDialog.dismiss();
                                            Toast.makeText(getActivity(),"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                                            Common.CurrentUser = response.body();
                                            getActivity().recreate();
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {

                                        }
                                    });

                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(),"Lỗi bất ngờ!",Toast.LENGTH_SHORT).show();
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
    }

    private void showRegisterPopup(final String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("REGISTER");

        LayoutInflater inflater = getLayoutInflater();
        View register_layout = inflater.inflate(R.layout.popup_register_layout, null);

        final MaterialEditText edt_name = register_layout.findViewById(R.id.register_name);
        final MaterialEditText edt_address = register_layout.findViewById(R.id.register_address);
        final MaterialEditText edt_birthday = register_layout.findViewById(R.id.register_birthday);

        Button btn_continue = register_layout.findViewById(R.id.register_btn_continue);

        edt_birthday.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog waitingdialog = new SpotsDialog.Builder().setContext(getActivity()).build();
                waitingdialog.setMessage("Please waiting...");
                waitingdialog.show();

                if(!TextUtils.isEmpty(edt_address.getText().toString())
                        && !TextUtils.isEmpty(edt_birthday.getText().toString())
                        && !TextUtils.isEmpty(edt_name.getText().toString())){
                    mService.registerUser(s,
                            edt_name.getText().toString(),
                            edt_address.getText().toString(),
                            edt_birthday.getText().toString())
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    waitingdialog.dismiss();
                                    User user = response.body();
                                    if(TextUtils.isEmpty(user.getError_msg())){
                                        dialog.dismiss();
                                        Toast.makeText(getActivity(),"Đăng kí thành công!",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    waitingdialog.dismiss();
                                }
                            });
                }
                else {
                    waitingdialog.dismiss();
                    Toast.makeText(getActivity(),"Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }
}
