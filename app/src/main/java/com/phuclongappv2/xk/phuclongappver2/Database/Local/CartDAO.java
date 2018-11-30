package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface CartDAO {
    @Query("SELECT EXISTS(SELECT 1 FROM Cart WHERE id=:itemId)")
    int isCart(int itemId);

    @Query("SELECT * FROM Cart")
    List<Cart> getCartItems();

    @Query("SELECT * FROM Cart WHERE id = :cartItemID")
    List<Cart> getCartById(int cartItemID);

    @Query("DELETE FROM Cart WHERE id = :cartItemID")
    void deleteCartById(int cartItemID);

    @Query("SELECT * FROM Cart WHERE idUser = :userID")
    List<Cart> getCartByUserId(String userID);

    @Query("SELECT COUNT(*) FROM Cart")
    int countCartItem();

    @Insert
    void insertCart(Cart... carts);

    @Query("DELETE FROM Cart")
    void emptyCart();

    @Update
    void updateCart(Cart... carts);

    @Delete
    void deleteCartItem(Cart cart);
}
