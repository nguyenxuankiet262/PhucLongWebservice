package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.os.Handler;
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

import com.phuclongappv2.xk.phuclongappver2.Adapter.NewsAdapter;
import com.phuclongappv2.xk.phuclongappver2.Interface.ILoadMore;
import com.phuclongappv2.xk.phuclongappver2.Model.News;
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
public class FragmentNotificationDetail extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    RelativeLayout empty_layout;
    RecyclerView recyclerView;

    NewsAdapter adapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IPhucLongAPI mService;
    SwipeRefreshLayout swipeRefreshLayout;
    int count = 3;

    public FragmentNotificationDetail() {
        // Required empty public constructor
    }

    public static FragmentNotificationDetail newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FragmentNotificationDetail fragment = new FragmentNotificationDetail();
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
        return inflater.inflate(R.layout.fragment_notification_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mService = Common.getAPI();

        empty_layout = view.findViewById(R.id.empty_noty);
        recyclerView = view.findViewById(R.id.list_notification);
        empty_layout.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout_noti);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getActivity())) {
                    loadNotification(mPage);
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
                    loadNotification(mPage);
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(getActivity(), "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    private void loadNotification(int page) {
        if(page == 1) {
            compositeDisposable.add(mService.getNews(page,3)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<News>>() {
                        @Override
                        public void accept(List<News> newsList) throws Exception {
                            if (newsList.size() != 0) {
                                empty_layout.setVisibility(View.GONE);
                                displayNoti(newsList);
                            } else {
                                empty_layout.setVisibility(View.VISIBLE);
                            }
                        }
                    })
            );
        }
        else{
            compositeDisposable.add(mService.getAllNews(3)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<News>>() {
                        @Override
                        public void accept(List<News> newsList) throws Exception {
                            displayAllNoty(newsList);
                        }
                    })
            );
        }
    }

    private void displayAllNoty(List<News> newsList) {
        adapter = new NewsAdapter(recyclerView,getActivity(),newsList);
        recyclerView.setAdapter(adapter);
        loadMore(adapter, newsList,0);

    }

    private void displayNoti(List<News> newsList) {
        adapter = new NewsAdapter(recyclerView,getActivity(),newsList);
        recyclerView.setAdapter(adapter);
        loadMore(adapter, newsList,1);
    }

    private void loadMore(final NewsAdapter adapter, final List<News> newsList, final int mPage) {
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if(newsList.size() <= 30) // Change max size
                {
                    count += 3;
                    newsList.add(null);
                    adapter.notifyItemInserted(newsList.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(Common.isConnectedToInternet(getActivity())) {
                                if (mPage == 1) {
                                    compositeDisposable.add(mService.getNews(mPage, count)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new Consumer<List<News>>() {
                                                @Override
                                                public void accept(List<News> List) throws Exception {
                                                    newsList.remove(newsList.size() - 1); // Remove thằng null khi nãy ra
                                                    Log.d("EEEA", List.size() + "");
                                                    adapter.notifyItemRemoved(newsList.size()); // Báo là có sự thay đổi
                                                    adapter.notifyItemRangeChanged(newsList.size(), newsList.size());
                                                    newsList.clear();
                                                    for (int i = 0; i < List.size(); i++) {
                                                        newsList.add(List.get(i));
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    adapter.setLoaded();
                                                }
                                            })
                                    );
                                } else {
                                    compositeDisposable.add(mService.getAllNews(count)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.io())
                                            .subscribe(new Consumer<List<News>>() {
                                                @Override
                                                public void accept(List<News> List) throws Exception {
                                                    newsList.remove(newsList.size() - 1); // Remove thằng null khi nãy ra
                                                    Log.d("EEEA", List.size() + "");
                                                    adapter.notifyItemRemoved(newsList.size()); // Báo là có sự thay đổi
                                                    adapter.notifyItemRangeChanged(newsList.size(), newsList.size());
                                                    newsList.clear();
                                                    for (int i = 0; i < List.size(); i++) {
                                                        newsList.add(List.get(i));
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    adapter.setLoaded();
                                                }
                                            })
                                    );
                                }
                            }
                            else{
                                newsList.remove(newsList.size() - 1); // Remove thằng null khi nãy ra
                                adapter.notifyItemRemoved(newsList.size()); // Báo là có sự thay đổi
                                adapter.notifyItemRangeChanged(newsList.size(), newsList.size());
                                adapter.setLoaded();
                                Toast.makeText(getActivity(), "Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },2000);
                }else{

                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
    }
}
