package com.phuclongappv2.xk.phuclongappver2.Database.DataSource;


import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public class FavoriteRepository implements IFavoriteDataSource{

    private IFavoriteDataSource favoriteDataSource;
    public FavoriteRepository(IFavoriteDataSource favoriteDataSource){
        this.favoriteDataSource = favoriteDataSource;
    }

    private static FavoriteRepository instance;
    public static FavoriteRepository getInstance(IFavoriteDataSource favoriteDataSource){
        if(instance == null){
            instance = new FavoriteRepository(favoriteDataSource);
        }
        return instance;
    }

    @Override
    public Flowable<List<Favorite>> getFavItems() {
        return favoriteDataSource.getFavItems();
    }

    @Override
    public Flowable<List<Favorite>> getFavItemsByUserID(String userID){
        return favoriteDataSource.getFavItemsByUserID(userID);
    }

    @Override
    public int isFavorite(int itemId, String userId) {
        return favoriteDataSource.isFavorite(itemId, userId);
    }

    @Override
    public void insertCart(Favorite... favorites) {
        favoriteDataSource.insertCart(favorites);
    }

    @Override
    public void deleteFavItem(Favorite favorite) {
        favoriteDataSource.deleteFavItem(favorite);
    }

    @Override
    public int countFavItem(String userID) {
        return favoriteDataSource.countFavItem(userID);
    }
}
