package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.phuclongappv2.xk.phuclongappver2.Adapter.FavoriteAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorite extends Fragment {

    private RecyclerView list_fav;
    FrameLayout favLayout;
    RelativeLayout emptyLayout, loginLayout;
    CoordinatorLayout existLayout;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        loginLayout = view.findViewById(R.id.login_fav_layout);
        Common.parentFavLayout = view.findViewById(R.id.myCoordinatorLayout);
        list_fav = view.findViewById(R.id.list_favorite);
        list_fav.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        list_fav.setHasFixedSize(true);
        favLayout = view.findViewById(R.id.fav_layout);
        existLayout = view.findViewById(R.id.myCoordinatorLayout);
        if(Common.CurrentUser != null) {
            loginLayout.setVisibility(View.GONE);
            loadFavItem();
        }
        else{
            existLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void loadFavItem() {
        compositeDisposable.add(Common.favoriteRepository.getFavItemsByUserID(Common.CurrentUser.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Favorite>>() {
                    @Override
                    public void accept(List<Favorite> favorites) throws Exception {
                        if (Common.favoriteRepository.countFavItem(Common.CurrentUser.getId()) != 0) {
                            existLayout.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                        }
                        else{
                            emptyLayout.setVisibility(View.VISIBLE);
                        }
                        displayFav(favorites);
                    }
                })
        );
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
