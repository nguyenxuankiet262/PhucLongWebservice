package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.ISuggestDrinkDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;

import java.util.List;

import io.reactivex.Flowable;

public class SuggestDrinkDataSource implements ISuggestDrinkDataSource{
    private SuggestDrinkDAO suggestDrinkDAO;
    private static SuggestDrinkDataSource instance;

    public SuggestDrinkDataSource(SuggestDrinkDAO suggestDrinkDAO){
        this.suggestDrinkDAO = suggestDrinkDAO;
    }

    public static SuggestDrinkDataSource getInstance(SuggestDrinkDAO suggestDrinkDAO) {
        if(instance == null){
            instance = new SuggestDrinkDataSource(suggestDrinkDAO);
        }
        return instance;
    }

    @Override
    public int isSuggestDrink(int itemId) {
        return suggestDrinkDAO.isSuggestDrink(itemId);
    }

    @Override
    public Flowable<List<SuggestDrink>> getSDItems() {
        return suggestDrinkDAO.getSDItems();
    }

    public SuggestDrink getFirstItems(){
        return suggestDrinkDAO.getFirstItems();
    }

    @Override
    public void insertSuggestDrink(SuggestDrink... suggestDrinks) {
        suggestDrinkDAO.insertSuggestDrink(suggestDrinks);
    }

    @Override
    public void deleteSuggestDrinkItem(SuggestDrink suggestDrink) {
        suggestDrinkDAO.deleteSuggestDrinkItem(suggestDrink);
    }

    @Override
    public int countSuggestDrinkItem() {
        return suggestDrinkDAO.countSuggestDrinkItem();
    }
}
