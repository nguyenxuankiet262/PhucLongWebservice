package com.phuclongappv2.xk.phuclongappver2;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private Toolbar toolbar;
    //Notification
    NotificationBadge badgeCart;
    ImageView cartBtn;

    FButton btn_login;
    RelativeLayout login_layout;

    LinearLayout personal_info_layout, detail_info_layout, history_layout, feedback_layout, signout_layout;

    MaterialEditText name_user, address_user;

    ImageView btn_open_info;

    TextView phone_number;

    Switch btn_noti;

    SwipeRefreshLayout swipeRefreshLayout;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.main_tool_bar);
        toolbar.inflateMenu(R.menu.menu_main_toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.icon_search)
                {
                    Intent intent = new Intent(getActivity(),ActivitySearch.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_more);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);

        Menu menu = toolbar.getMenu();
        View item = menu.findItem(R.id.icon_cart_menu).getActionView();
        badgeCart = item.findViewById(R.id.badge);
        cartBtn = item.findViewById(R.id.cart_icon);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cartIntent = new Intent(getActivity(), ActivityCart.class);
                startActivity(cartIntent);
            }
        });
        updateCartCount();

        mService = Common.getAPI();

        //Ánh xạ
        personal_info_layout = view.findViewById(R.id.personal_info);
        detail_info_layout = view.findViewById(R.id.detail_info);
        history_layout = view.findViewById(R.id.my_history);
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

        setStatusEditText(name_user, false);
        setStatusEditText(address_user, false);

        feedback_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentFeedBack fragmentFeedBack = new FragmentFeedBack();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_more, fragmentFeedBack, "FeedBackFragment").addToBackStack(null).commit();
            }
        });

        btn_noti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(Common.CurrentUser != null) {
                        if(Common.isConnectedToInternet(getActivity())) {
                            mService.updateNews(Common.CurrentUser.getPhone(), 1).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Common.CurrentUser.setNoti_news(1);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(getActivity(), "Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    if(Common.CurrentUser != null) {
                        if(Common.isConnectedToInternet(getActivity())) {
                            mService.updateNews(Common.CurrentUser.getPhone(), 0).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Common.CurrentUser.setNoti_news(0);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                        else{
                            Toast.makeText(getActivity(),"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

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
                        if(Common.isConnectedToInternet(getActivity())) {
                            mService.getUser(Common.CurrentUser.getPhone()).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if(response.body().getActive() == 1){
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
                                    }
                                    else{
                                        alertDialog.dismiss();
                                        Toast.makeText(getActivity(), "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        }
                        else{
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(),"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
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
                        if(Common.isConnectedToInternet(getActivity())) {
                            Common.CurrentUser = null;
                            AccountKit.logOut();
                            final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(getActivity()).build();
                            alertDialog.setMessage("Please waiting...");
                            alertDialog.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                                    getActivity().recreate();
                                }
                            }, 2000);
                        }
                        else{
                            Toast.makeText(getActivity(),"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        login_layout = view.findViewById(R.id.login_layout);
        btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginPage(LoginType.PHONE);
            }
        });


        if (Common.CurrentUser != null) {
            login_layout.setVisibility(View.GONE);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(Common.isConnectedToInternet(getActivity())) {
                        loadInfo();
                        updateBadge();
                        updateHistory();
                        phone_number.setText(Common.CurrentUser.getPhone());
                        if(Common.CurrentUser.getNoti_news() == 0){
                            btn_noti.setChecked(false);
                        }
                        else{
                            btn_noti.setChecked(true);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    else{
                        Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    if(Common.isConnectedToInternet(getActivity())) {
                        loadInfo();
                        updateBadge();
                        updateHistory();
                        phone_number.setText(Common.CurrentUser.getPhone());
                        if(Common.CurrentUser.getNoti_news() == 0){
                            btn_noti.setChecked(false);
                        }
                        else{
                            btn_noti.setChecked(true);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    else{
                        Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });
        }
        else {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
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

    public void updateBadge() {
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
                            mService.registerUser(account.getPhoneNumber().toString(), "", "",1,1).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User user = response.body();
                                    if (user.getError_msg() != null && user.getError_msg().equals("true")) {
                                        mService.getUser(account.getPhoneNumber().toString()).enqueue(new Callback<User>() {
                                            @Override
                                            public void onResponse(Call<User> call, Response<User> response) {
                                                Log.d("EEE", response.body().getActive()+"");
                                                if(response.body().getActive() == 1){
                                                    Common.CurrentUser = response.body();
                                                    loadInfo();
                                                    updateBadge();
                                                    updateCartCount();
                                                    ((ActivityMain) getActivity()).updateNotificationHomeIcon();
                                                    updateTokenToServer();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            alertDialog.dismiss();
                                                            Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                            login_layout.setVisibility(View.GONE);
                                                        }
                                                    },2000);
                                                }
                                                else{
                                                    alertDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<User> call, Throwable t) {

                                            }
                                        });
                                    } else {
                                        Common.CurrentUser = user;
                                        loadInfo();
                                        updateBadge();
                                        ((ActivityMain) getActivity()).updateNotificationHomeIcon();
                                        updateTokenToServer();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertDialog.dismiss();
                                                Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                                getActivity().recreate();
                                            }
                                        },2000);
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

    public void updateCartCount() {
        if (badgeCart == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.cartRepository.countCartItem() == 0)
                    badgeCart.setVisibility(View.INVISIBLE);
                else {
                    badgeCart.setVisibility(View.VISIBLE);
                    badgeCart.setText(String.valueOf(Common.cartRepository.countCartItem()));
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_search:
                Intent intent = new Intent(getActivity(),ActivitySearch.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
        updateHistory();
    }
}
