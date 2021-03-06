package com.phuclongappv2.xk.phuclongappver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.phuclongappv2.xk.phuclongappver2.Adapter.DrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Adapter.RecentDrinkAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.SuggestDrinkRepository;
import com.phuclongappv2.xk.phuclongappver2.Database.Local.SuggestDrinkDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;
import com.phuclongappv2.xk.phuclongappver2.Model.Drink;
import com.phuclongappv2.xk.phuclongappver2.Model.Rating;
import com.phuclongappv2.xk.phuclongappver2.Retrofit.IPhucLongAPI;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.phuclongappv2.xk.phuclongappver2.Utils.Common.isConnectedToInternet;

public class ActivitySearch extends AppCompatActivity implements RatingDialogListener {

    MaterialSearchBar searchBar;
    ImageView btn_back;
    RelativeLayout relativeLayout;
    LinearLayout recentLayout;
    RecyclerView recentList, suggestList,searchList;
    DrinkAdapter drinkAdapter;
    NestedScrollView nestedScrollView;
    View view;
    SwipeRefreshLayout swipeRefreshLayout;

    IPhucLongAPI mService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mService = Common.getAPI();
        initDB();

        //2: đang trong activty search ko cần update badge cart
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
        swipeRefreshLayout = findViewById(R.id.swipe_layout_search);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark ,
                android.R.color.holo_orange_dark);

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    if (Common.suggestDrinkRepository.countSuggestDrinkItem() != 0) {
                        recentLayout.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                        loadRecentDrinks();
                    } else {
                        recentLayout.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    }
                    loadSuggestList();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivitySearch.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext())) {

                    if (Common.suggestDrinkRepository.countSuggestDrinkItem() != 0) {
                        recentLayout.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                        loadRecentDrinks();
                    } else {
                        recentLayout.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);
                    }
                    loadSuggestList();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    Toast.makeText(ActivitySearch.this, "Không thể kết nối mạng!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

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
                    Common.checkInSearchActivity = 1;
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
                    if(Common.isConnectedToInternet(getBaseContext())) {
                        startSearch(s);
                    }
                    else{
                        Toast.makeText(ActivitySearch.this,"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Common.checkInSearchActivity = 1;
                    searchList.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initDB() {
        Common.suggestDrinkRepository = SuggestDrinkRepository.getInstance(SuggestDrinkDataSource.getInstance(Common.drinkroomDatabase.suggestDrinkDAO()));
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
        Common.checkInSearchActivity = 2;
        if(Common.isConnectedToInternet(ActivitySearch.this)) {
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

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ActivitySearch.this);
        progressDialog.setTitle("Submitting...");
        progressDialog.setMessage("Please wait for a minute!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Rating rate = new Rating();
        rate.setDrinkID(Common.drinkID);
        rate.setRate(i);
        rate.setComment(s);
        Date date = new Date();
        rate.setDate(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
        if(Common.isConnectedToInternet(getBaseContext())) {
            mService.setRating(Common.CurrentUser.getPhone(), rate.getDrinkID(), rate.getRate(), rate.getComment(), rate.getDate())
                    .enqueue(new Callback<Rating>() {
                        @Override
                        public void onResponse(Call<Rating> call, Response<Rating> response) {
                            Rating rating = response.body();
                            if (TextUtils.isEmpty(rating.getError_msg())) {
                                progressDialog.dismiss();
                                Toast.makeText(ActivitySearch.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Rating> call, Throwable t) {
                            progressDialog.dismiss();
                            Log.d("EEEE", t.getMessage());
                            Toast.makeText(ActivitySearch.this, "Lỗi bất ngờ!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(),"Kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
