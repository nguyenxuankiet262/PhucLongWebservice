package com.phuclongappv2.xk.phuclongappver2;


import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.phuclongappv2.xk.phuclongappver2.Adapter.FavoriteAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.phuclongappv2.xk.phuclongappver2.R.color.colorPrimaryDark;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorite extends Fragment {

    private RecyclerView list_fav;
    FrameLayout favLayout;
    RelativeLayout emptyLayout;
    CoordinatorLayout existLayout;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FloatingActionButton floatingActionButton, all_fab, cold_fab, hot_fab;
    boolean checkFab = true;
    int index = 0;

    public FragmentFavorite() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emptyLayout = view.findViewById(R.id.empty_fav_layout);
        Common.parentFavLayout = view.findViewById(R.id.myCoordinatorLayout);
        floatingActionButton = view.findViewById(R.id.FAB_favorite);
        all_fab = view.findViewById(R.id.all_fab);
        cold_fab = view.findViewById(R.id.cold_fab);
        hot_fab = view.findViewById(R.id.hot_fab);
        hideFab();
        list_fav = view.findViewById(R.id.list_favorite);
        list_fav.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        list_fav.setHasFixedSize(true);
        favLayout = view.findViewById(R.id.fav_layout);
        existLayout = view.findViewById(R.id.myCoordinatorLayout);
        loadFavItem(index);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkFab) {
                    showFab();
                    checkFab = false;
                }
                else{
                    hideFab();
                    checkFab = true;
                }
            }
        });
        all_fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                hideFab();
                checkFab = true;
                index = 0;
                loadFavItem(index);
                floatingActionButton.setImageResource(R.drawable.all_icon_24);
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }
        });
        hot_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab();
                checkFab = true;
                index = 2;
                loadFavItem(index);
                floatingActionButton.setImageResource(R.drawable.hot_icon_24);
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorCancel)));
            }
        });
        cold_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab();
                checkFab = true;
                index = 1;
                loadFavItem(index);
                floatingActionButton.setImageResource(R.drawable.cold_icon_24);
                floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOTW)));
            }
        });
    }

    private void showFab() {
        all_fab.show();;
        cold_fab.show();
        hot_fab.show();
    }

    private void hideFab() {
        all_fab.hide();
        cold_fab.hide();
        hot_fab.hide();
    }

    private void loadFavItem(int i) {
        if(i == 0) {
            compositeDisposable.add(Common.favoriteRepository.getFavItems()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Favorite>>() {
                        @Override
                        public void accept(List<Favorite> favorites) throws Exception {
                            if (Common.favoriteRepository.countFavItem() != 0) { ;
                                existLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            } else {
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            displayFav(favorites);
                        }
                    })
            );
        }
        else if(i == 1){
            compositeDisposable.add(Common.favoriteRepository.getColdFav()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Favorite>>() {
                        @Override
                        public void accept(List<Favorite> favorites) throws Exception {
                            if (favorites.size() != 0) {
                                existLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);

                            } else {
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            displayFav(favorites);
                        }
                    })
            );
        }
        else{
            compositeDisposable.add(Common.favoriteRepository.getHotFav()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<List<Favorite>>() {
                        @Override
                        public void accept(List<Favorite> favorites) throws Exception {
                            if (favorites.size() != 0) {
                                existLayout.setVisibility(View.VISIBLE);
                                emptyLayout.setVisibility(View.GONE);
                            } else {
                                emptyLayout.setVisibility(View.VISIBLE);
                            }
                            displayFav(favorites);
                        }
                    })
            );
        }
    }

    private void displayFav(List<Favorite> favorites) {
        FavoriteAdapter adapter = new FavoriteAdapter(getActivity(), favorites, this);
        list_fav.setAdapter(adapter);
    }
    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
