package com.phuclongappv2.xk.phuclongappver2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nex3z.notificationbadge.NotificationBadge;
import com.phuclongappv2.xk.phuclongappver2.Model.CheckUserResponse;
import com.phuclongappv2.xk.phuclongappver2.Model.Token;
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

    LinearLayout personal_info_layout, detail_info_layout, history_layout, paypal_layout, credit_layout, feedback_layout, signout_layout;

    MaterialEditText name_user, address_user;

    ImageView btn_open_info;

    TextView phone_number;

    Switch btn_noti;

    NotificationBadge notificationBadge;

    RelativeLayout newBadge;

    FButton save_btn;

    private static final int REQUEST_CODE = 1000;

    IPhucLongAPI mService;

    public int check_dropdown = 0;
    public int check_edit_info = 0;


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

        //Ánh xạ
        personal_info_layout = view.findViewById(R.id.personal_info);
        detail_info_layout = view.findViewById(R.id.detail_info);
        history_layout = view.findViewById(R.id.my_history);
        paypal_layout = view.findViewById(R.id.payment_paypal);
        credit_layout = view.findViewById(R.id.payment_momo);
        feedback_layout = view.findViewById(R.id.feedback_layout);
        signout_layout = view.findViewById(R.id.signout_layout);

        name_user = view.findViewById(R.id.name_info);
        address_user = view.findViewById(R.id.address_info);

        btn_open_info = view.findViewById(R.id.btn_open_info);
        phone_number = view.findViewById(R.id.phone_number);
        btn_noti = view.findViewById(R.id.btn_noti);
        notificationBadge = view.findViewById(R.id.badge);
        newBadge = view.findViewById(R.id.new_layout);

        detail_info_layout.setVisibility(View.GONE);

        save_btn = view.findViewById(R.id.save_btn);

        if (Common.CurrentUser != null) {
            loadInfo();
            updateBadge();
            updateHistory();
            phone_number.setText(Common.CurrentUser.getPhone());
        }
        setStatusEditText(name_user, false);
        setStatusEditText(address_user, false);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_edit_info == 0) {
                    check_edit_info = 1;
                    save_btn.setText("Lưu");
                    setStatusEditText(name_user, true);
                    setStatusEditText(address_user, true);
                } else {
                    final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
                    alertDialog.setMessage("Please waiting...");
                    alertDialog.show();
                    if (!TextUtils.isEmpty(name_user.getText()) && !TextUtils.isEmpty(address_user.getText())) {
                        mService.updateUser(Common.CurrentUser.getPhone(), name_user.getText().toString(), address_user.getText().toString())
                                .enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                        alertDialog.dismiss();
                                        Common.CurrentUser.setName(response.body().getName());
                                        Common.CurrentUser.setAddress(response.body().getAddress());
                                        Toast.makeText(getActivity(), "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                                        updateBadge();
                                        ((ActivityMain) getActivity()).updateNotificationHomeIcon();
                                        detail_info_layout.setVisibility(View.GONE);
                                        btn_open_info.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                                        check_dropdown = 0;
                                        check_edit_info = 0;
                                        setStatusEditText(name_user, false);
                                        setStatusEditText(address_user, false);
                                        save_btn.setText("Chỉnh sửa");

                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {

                                    }
                                });
                    } else {
                        alertDialog.dismiss();
                        Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        personal_info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_dropdown == 0) {
                    detail_info_layout.setVisibility(View.VISIBLE);
                    btn_open_info.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    check_dropdown = 1;
                } else {
                    detail_info_layout.setVisibility(View.GONE);
                    btn_open_info.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
                    check_dropdown = 0;
                    check_edit_info = 0;
                    setStatusEditText(name_user, false);
                    setStatusEditText(address_user, false);
                    save_btn.setText("Chỉnh sửa");
                }
            }
        });

        history_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHistory fragmentHistory = new FragmentHistory();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_more, fragmentHistory, "HistoryFragment").addToBackStack(null).commit();
                mService.updateHistory(Common.CurrentUser.getPhone(),0).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Common.CurrentUser.setNoti_history(0);
                        updateHistory();
                        ((ActivityMain) getActivity()).updateNotificationHomeIcon();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }
        });

        signout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Bạn có muốn đăng xuất không?");
                builder.setCancelable(false);
                builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Chấp nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Common.CurrentUser = null;
                        AccountKit.logOut();
                        getActivity().recreate();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        login_layout = view.findViewById(R.id.login_layout);
        if (Common.CurrentUser != null) {
            login_layout.setVisibility(View.GONE);
        }
        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.PHONE);
            }
        });
        if (Common.CurrentUser != null) {
            login_layout.setVisibility(View.GONE);
        } else {
            login_layout.setVisibility(View.VISIBLE);
        }
    }

    public void updateHistory() {
        if(Common.CurrentUser != null) {
            if (Common.CurrentUser.getNoti_history() == 0) {
                newBadge.setVisibility(View.INVISIBLE);
            } else {
                newBadge.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHistory();
    }

    private void setStatusEditText(MaterialEditText editText, boolean status) {
        if (status == false) {
            editText.setClickable(false);
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        } else {
            editText.setClickable(true);
            editText.setCursorVisible(true);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
        }
    }

    private void loadInfo() {
        if (!TextUtils.isEmpty(Common.CurrentUser.getName()) && !TextUtils.isEmpty(Common.CurrentUser.getAddress()))
            name_user.setText(Common.CurrentUser.getName());
        address_user.setText(Common.CurrentUser.getAddress());
    }

    private void updateBadge() {
        if (notificationBadge == null) return;
        if (TextUtils.isEmpty(Common.CurrentUser.getName()) && TextUtils.isEmpty(Common.CurrentUser.getAddress())) {
            notificationBadge.setText("2");
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }

    public void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null) {
                Toast.makeText(getActivity(), "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_LONG).show();
            } else if (result.wasCancelled()) {

            } else {
                if (result.getAccessToken() != null) {
                    final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
                    alertDialog.setMessage("Please waiting...");
                    alertDialog.show();

                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.registerUser(account.getPhoneNumber().toString(), "", "").enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User user = response.body();
                                    if (user.getError_msg() != null && user.getError_msg().equals("true")) {
                                        mService.getUser(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Common.CurrentUser = response.body();
                                                ;
                                                alertDialog.dismiss();
                                                Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                loadInfo();
                                                updateBadge();
                                                ((ActivityMain) getActivity()).updateNotificationHomeIcon();
                                                updateTokenToServer();
                                                getActivity().recreate();
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {

                                            }
                                        });
                                    } else {
                                        Common.CurrentUser = response.body();
                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        getActivity().recreate();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), "Lỗi bất ngờ!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

                if (!TextUtils.isEmpty(edt_address.getText().toString())
                        && !TextUtils.isEmpty(edt_birthday.getText().toString())
                        && !TextUtils.isEmpty(edt_name.getText().toString())) {
                    mService.registerUser(s,
                            edt_name.getText().toString(),
                            edt_address.getText().toString())
                            .enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    waitingdialog.dismiss();
                                    User user = response.body();
                                    if (TextUtils.isEmpty(user.getError_msg())) {
                                        dialog.dismiss();
                                        Toast.makeText(getActivity(), "Đăng kí thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    waitingdialog.dismiss();
                                }
                            });
                } else {
                    waitingdialog.dismiss();
                    Toast.makeText(getActivity(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();

    }
}
