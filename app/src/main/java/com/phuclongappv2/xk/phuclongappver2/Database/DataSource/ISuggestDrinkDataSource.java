package com.phuclongappv2.xk.phuclongappver2.Database.DataSource;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;

import java.util.List;

import io.reactivex.Flowable;

public interface ISuggestDrinkDataSource {
    int isSuggestDrink(String itemId);
    Flowable<List<SuggestDrink>> getSDItems();
    SuggestDrink getFirstItems();
    void insertSuggestDrink(SuggestDrink... suggestDrinks);
    void deleteSuggestDrinkItem(SuggestDrink suggestDrink);
    int countSuggestDrinkItem();
}
