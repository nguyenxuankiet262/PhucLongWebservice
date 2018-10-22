package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FavoriteDAO {
    @Query("SELECT * FROM Favorite")
    Flowable<List<Favorite>> getFavItems();

    @Query("SELECT * FROM Favorite WHERE idUser=:userID")
    Flowable<List<Favorite>> getFavItemsByUserID(String userID);

    @Query("SELECT * FROM Favorite WHERE imagecold != 'empty'")
    Flowable<List<Favorite>> getColdFav();

    @Query("SELECT * FROM Favorite WHERE imagehot != 'empty'")
    Flowable<List<Favorite>> getHotFav();

    @Query("SELECT EXISTS(SELECT 1 FROM Favorite WHERE id=:itemId)")
    int isFavorite(int itemId);

    @Insert
    void insertCart(Favorite... favorites);

    @Delete
    void deleteFavItem(Favorite favorite);

    @Query("SELECT COUNT(*) FROM Favorite")
    int countFavItem();
}
