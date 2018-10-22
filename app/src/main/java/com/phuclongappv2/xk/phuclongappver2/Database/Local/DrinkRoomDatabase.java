package com.phuclongappv2.xk.phuclongappver2.Database.Local;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.SuggestDrink;

@Database(entities = {Cart.class,Favorite.class, SuggestDrink.class}, version = 1)
public abstract class DrinkRoomDatabase extends RoomDatabase {
    private static DrinkRoomDatabase sCartDatabase;
    public static final String DATABASE_NAME = "Room-database";

    public abstract CartDAO cartDAO();
    public abstract FavoriteDAO favoriteDAO();
    public abstract SuggestDrinkDAO suggestDrinkDAO();

    public static DrinkRoomDatabase getInstance(Context context) {
        if (sCartDatabase == null) {
            sCartDatabase = Room.databaseBuilder(context, DrinkRoomDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sCartDatabase;
    }

}
