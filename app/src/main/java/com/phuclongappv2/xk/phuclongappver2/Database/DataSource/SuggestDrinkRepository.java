package com.phuclongappv2.xk.phuclongappver2.Database.DataSource;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;

import java.util.List;

import io.reactivex.Flowable;

public class SuggestDrinkRepository implements ISuggestDrinkDataSource {

    private ISuggestDrinkDataSource suggestDrinkDataSource;
    public SuggestDrinkRepository(ISuggestDrinkDataSource suggestDrinkDataSource){
        this.suggestDrinkDataSource = suggestDrinkDataSource;
    }

    private static SuggestDrinkRepository instance;
    public static SuggestDrinkRepository getInstance(ISuggestDrinkDataSource suggestDrinkDataSource){
        if(instance == null){
            instance = new SuggestDrinkRepository(suggestDrinkDataSource);
        }
        return instance;
    }

    @Override
    public int isSuggestDrink(int itemId) {
        return suggestDrinkDataSource.isSuggestDrink(itemId);
    }

    @Override
    public Flowable<List<SuggestDrink>> getSDItems() {
        return suggestDrinkDataSource.getSDItems();
    }

    @Override
    public  SuggestDrink getFirstItems(){
        return  suggestDrinkDataSource.getFirstItems();
    }

    @Override
    public void insertSuggestDrink(SuggestDrink... suggestDrinks) {
        suggestDrinkDataSource.insertSuggestDrink(suggestDrinks);
    }

    @Override
    public void deleteSuggestDrinkItem(SuggestDrink suggestDrink) {
        suggestDrinkDataSource.deleteSuggestDrinkItem(suggestDrink);
    }

    @Override
    public int countSuggestDrinkItem() {
        return suggestDrinkDataSource.countSuggestDrinkItem();
    }
}
