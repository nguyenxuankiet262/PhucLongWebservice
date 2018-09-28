package com.phuclongappv2.xk.phuclongappver2;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.phuclongappv2.xk.phuclongappver2.Adapter.DrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Adapter.RecentDrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ActivitySearch extends AppCompatActivity {

    MaterialSearchBar searchBar;
    ImageView btn_back;
    RelativeLayout relativeLayout;
    LinearLayout recentLayout;
    RecyclerView recentList, suggestList,searchList;
    DrinkAdapter drinkAdapter;
    RecentDrinkAdapter recentDrinkAdapter;
    NestedScrollView nestedScrollView;
    View view;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mService = Common.getAPI();
        Common.checkInSearchActivity = 1;

        view = findViewById(R.id.line_search);
        btn_back = findViewById(R.id.btn_back_search);
        searchBar = findViewById(R.id.search_bar);
        relativeLayout = findViewById(R.id.back_btn_layout);
        recentLayout = findViewById(R.id.recent_list_layout);
        recentList = findViewById(R.id.recent_list);
        suggestList = findViewById(R.id.suggest_list);
        searchList = findViewById(R.id.search_drink_list);
        nestedScrollView = findViewById(R.id.nestedscrollview);

        suggestList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        suggestList.setHasFixedSize(true);
        suggestList.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recentList.setLayoutManager(mLayoutManager);
        //recentList.setNestedScrollingEnabled(false);
        searchList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        searchList.setHasFixedSize(true);

        if(Common.suggestDrinkRepository.countSuggestDrinkItem() != 0){
            recentLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            loadRecentDrinks();
        }
        else{
            recentLayout.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.checkInSearchActivity = 0;
                onBackPressed();
            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    searchList.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                }
                else{
                    relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Toast.makeText(ActivitySearch.this,"2",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                //Toast.makeText(ActivitySearch.this,"3",Toast.LENGTH_SHORT).show();
            }
        });
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0) {
                    nestedScrollView.setVisibility(View.GONE);
                    searchList.setVisibility(View.VISIBLE);
                    startSearch(s);
                }
                else{
                    searchList.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadSuggestList();
    }

    private void loadRecentDrinks() {
        compositeDisposable.add(Common.suggestDrinkRepository.getSDItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<SuggestDrink>>() {
                    @Override
                    public void accept(List<SuggestDrink> suggestDrinks) throws Exception {
                        displayRecentDrink(suggestDrinks);
                    }
                })
        );
    }

    private void displayRecentDrink(List<SuggestDrink> suggestDrinks) {
        RecentDrinkAdapter adapter = new RecentDrinkAdapter(this,suggestDrinks);
        recentList.setAdapter(adapter);
    }

    private void startSearch(CharSequence s) {
        compositeDisposable.add(mService.getDrinkByName(s.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        loadSearchDrinks(drinks);
                    }
                })
        );
    }

    private void loadSearchDrinks(List<Drink> drinks) {
        drinkAdapter = new DrinkAdapter(this, drinks,this);
        searchList.setAdapter(drinkAdapter);
    }

    private void loadSuggestList() {
        compositeDisposable.add(mService.getTenRandomDrinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        displaySuggestDrink(drinks);
                    }
                })
        );
    }

    private void displaySuggestDrink(List<Drink> drinks) {
        drinkAdapter = new DrinkAdapter(this, drinks,this);
        suggestList.setAdapter(drinkAdapter);
    }
    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Common.checkInSearchActivity = 0;
    }
}
