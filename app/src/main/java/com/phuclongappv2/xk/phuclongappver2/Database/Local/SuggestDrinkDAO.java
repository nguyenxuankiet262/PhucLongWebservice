package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface SuggestDrinkDAO {
    @Query("SELECT EXISTS(SELECT 1 FROM SuggestDrink WHERE ID=:itemId)")
    int isSuggestDrink(int itemId);

    @Query("SELECT * FROM SuggestDrink")
    Flowable<List<SuggestDrink>> getSDItems();

    @Query("SELECT * FROM SuggestDrink LIMIT 1")
    SuggestDrink getFirstItems();

    @Insert
    void insertSuggestDrink(SuggestDrink... suggestDrinks);

    @Delete
    void deleteSuggestDrinkItem(SuggestDrink suggestDrink);

    @Query("SELECT COUNT(*) FROM SuggestDrink")
    int countSuggestDrinkItem();
}
