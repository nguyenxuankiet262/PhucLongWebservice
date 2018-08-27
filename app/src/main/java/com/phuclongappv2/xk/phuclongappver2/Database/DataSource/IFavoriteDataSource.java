package com.phuclongappv2.xk.phuclongappver2.Database.DataSource;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavoriteDataSource {
    Flowable<List<Favorite>> getFavItems();
    Flowable<List<Favorite>> getFavItemsByUserID(String userID);
    int isFavorite(int itemId, String userId);
    void insertCart(Favorite... favorites);
    void deleteFavItem(Favorite favorite);
    int countFavItem(String userID);
}
