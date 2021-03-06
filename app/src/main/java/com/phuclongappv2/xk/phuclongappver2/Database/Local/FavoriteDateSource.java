package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.IFavoriteDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Favorite;

import java.util.List;

import io.reactivex.Flowable;

public class FavoriteDateSource implements IFavoriteDataSource {
    private FavoriteDAO favoriteDAO;
    private static FavoriteDateSource instance;

    public FavoriteDateSource(FavoriteDAO favoriteDAO){
        this.favoriteDAO = favoriteDAO;
    }

    public static FavoriteDateSource getInstance(FavoriteDAO favoriteDAO) {
        if(instance == null){
            instance = new FavoriteDateSource(favoriteDAO);
        }
        return instance;
    }

    @Override
    public Flowable<List<Favorite>> getFavItems() {
        return favoriteDAO.getFavItems();
    }

    @Override
    public Flowable<List<Favorite>> getFavItemsByUserID(String userID) {
        return favoriteDAO.getFavItemsByUserID(userID);
    }

    @Override
    public Flowable<List<Favorite>> getColdFav(){
        return favoriteDAO.getColdFav();
    }

    @Override
    public Flowable<List<Favorite>> getHotFav(){
        return favoriteDAO.getHotFav();
    }

    @Override
    public int isFavorite(int itemId) {
        return favoriteDAO.isFavorite(itemId);
    }

    @Override
    public void insertCart(Favorite... favorites) {
        favoriteDAO.insertCart(favorites);
    }

    @Override
    public void deleteFavItem(Favorite favorite) {
        favoriteDAO.deleteFavItem(favorite);
    }

    @Override
    public int countFavItem() {
        return favoriteDAO.countFavItem();
    }
}
