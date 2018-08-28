package com.phuclongappv2.xk.phuclongappver2;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phuclongappv2.xk.phuclongappver2.Adapter.DrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDrink extends Fragment {

    RecyclerView list_drink;
    CollapsingToolbarLayout collapsingToolbarLayout;

    //Adapter
    DrinkAdapter adapter;

    //Extra
    String id_cate,name_cate;

    Toolbar toolbar;

    IPhucLongAPI mService;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drink, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mService = Common.getAPI();

        list_drink = view.findViewById(R.id.listDrink);
        list_drink.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        list_drink.setHasFixedSize(true);

        collapsingToolbarLayout = view.findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getArguments() != null){
            id_cate = getArguments().getString("CategoryId");
            name_cate = getArguments().getString("CategoryName");
        }
        if(!id_cate.isEmpty() && id_cate != null){
            collapsingToolbarLayout.setTitle(name_cate);
        }

        loadDrink(id_cate);
    }

    private void loadDrink(String id_cate) {
        compositeDisposable.add(mService.getDrink(id_cate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displayDrink(drinks);
                    }
                }));
    }
    private void displayDrink(List<Drink> drinks) {
        adapter = new DrinkAdapter(getActivity(), drinks,getActivity());
        list_drink.setAdapter(adapter);
    }

}
