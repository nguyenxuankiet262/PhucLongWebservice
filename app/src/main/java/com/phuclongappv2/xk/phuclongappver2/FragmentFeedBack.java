package com.phuclongappv2.xk.phuclongappver2;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.phuclongappv2.xk.phuclongappver2.Adapter.FeedbackAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Feedback;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFeedBack extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    Button btn_send_feedback;
    EditText content_feedback;
    AlertDialog alertDialog;
    RelativeLayout empty_layout;
    FeedbackAdapter adapter;
    RecyclerView recyclerView;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentFeedBack() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_back, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.BackPressE = 1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mService = Common.getAPI();
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_feedback);
        empty_layout = view.findViewById(R.id.empty_feedback_layout);
        toolbar = view.findViewById(R.id.tool_bar_feedback);
        toolbar.inflateMenu(R.menu.menu_feedback_toolbar);
        recyclerView = view.findViewById(R.id.list_feedback);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.icon_add){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_add_feedback_layout, null);
                    btn_send_feedback = itemView.findViewById(R.id.btn_send_feedback);
                    content_feedback = itemView.findViewById(R.id.content_feedback_popup);

                    btn_send_feedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(content_feedback.getText().toString())) {
                                if(Common.isConnectedToInternet(getActivity())) {
                                    mService.getUser(Common.CurrentUser.getPhone()).enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            if(response.body().getActive() == 1){
                                                InsertFeedback(content_feedback.getText().toString());
                                            }
                                            else{
                                                Toast.makeText(getActivity(), "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                                            }
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
                            else{
                                Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setView(itemView);
                    alertDialog = builder.show();

                }
                return false;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity())) {
                    loadFeedback();
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
                    loadFeedback();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void loadFeedback() {
        compositeDisposable.add(mService.getFeedback(Common.CurrentUser.getPhone())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Feedback>>() {
                    @Override
                    public void accept(List<Feedback> feedbackList) throws Exception {
                        if(feedbackList.size() == 0) {
                            empty_layout.setVisibility(View.VISIBLE);
                        }
                        else{
                            empty_layout.setVisibility(View.GONE);
                        }
                        displayFeedback(feedbackList);
                    }
                }));
    }

    private void displayFeedback(List<Feedback> feedbackList) {
        adapter = new FeedbackAdapter(getActivity(), feedbackList);
        recyclerView.setAdapter(adapter);

    }

    private void InsertFeedback(String s) {
        mService.insertFeedback(Common.CurrentUser.getPhone(),s)
                .enqueue(new Callback<Feedback>() {
                    @Override
                    public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                        alertDialog.dismiss();
                        loadFeedback();
                        Toast.makeText(getActivity(), "Cảm ơn bạn đã gửi phản hồi!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Feedback> call, Throwable t) {

                    }
                });
    }
    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

}
