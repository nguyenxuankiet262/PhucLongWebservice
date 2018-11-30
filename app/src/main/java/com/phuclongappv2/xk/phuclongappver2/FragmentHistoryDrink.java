package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.phuclongappv2.xk.phuclongappver2.Adapter.OrderAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Order;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHistoryDrink extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    //Adapter
    OrderAdapter adapter;

    RelativeLayout empty_layout;
    RecyclerView recyclerView;

    SwipeRefreshLayout swipeRefreshLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IPhucLongAPI mService;

    public static FragmentHistoryDrink newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentHistoryDrink fragment = new FragmentHistoryDrink();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_drink, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mService = Common.getAPI();
        empty_layout = view.findViewById(R.id.empty_order);
        recyclerView = view.findViewById(R.id.list_history);
        empty_layout.setVisibility(View.GONE);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        Log.d("EEEA",mPage+"");

        recyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_history);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity())) {
                    if(Common.CurrentUser != null) {
                        loadHistory(mPage);
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
                    if(Common.CurrentUser != null) {
                        loadHistory(mPage);
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

    private void loadHistory(int mPage) {
        compositeDisposable.add(mService.getOrderByStatus(Common.CurrentUser.getPhone(),mPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Order>>() {
                    @Override
                    public void accept(List<Order> orders) throws Exception {
                        Log.d("EEE",orders.size()+"");
                        if(orders.size() != 0) {
                            empty_layout.setVisibility(View.GONE);
                            displayHistory(orders);
                        }
                        else{
                            empty_layout.setVisibility(View.VISIBLE);
                        }
                    }
                })
        );
    }

    private void displayHistory(List<Order> orders) {
        adapter = new OrderAdapter(getActivity(),orders);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
    }
}
