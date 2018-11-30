package com.phuclongappv2.xk.phuclongappver2.Database.Local;

import com.phuclongappv2.xk.phuclongappver2.Database.DataSource.ICartDataSource;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;

import java.util.List;

import io.reactivex.Flowable;

public class CartDataSource implements ICartDataSource {

    private CartDAO cartDAO;
    private static CartDataSource instance;

    public CartDataSource(CartDAO cartDAO){
        this.cartDAO = cartDAO;
    }
    public static CartDataSource getInstance(CartDAO cartDAO){
        if(instance == null){
            instance = new CartDataSource(cartDAO);
        }
        return instance;
    }

    @Override
    public int isCart(int itemId) {
        return cartDAO.isCart(itemId);
    }

    @Override
    public List<Cart> getCartItems() {
        return cartDAO.getCartItems();
    }

    @Override
    public List<Cart> getCartById(int cartItemID) {
        return cartDAO.getCartById(cartItemID);
    }

    @Override
    public void deleteCartById(int cartItemID) {
        cartDAO.deleteCartById(cartItemID);
    }

    @Override
    public List<Cart> getCartByUserId(String userID) {
        return cartDAO.getCartByUserId(userID);
    }

    @Override
    public int countCartItem() {
        return cartDAO.countCartItem();
    }

    @Override
    public void insertCart(Cart... carts) {
        cartDAO.insertCart(carts);
    }

    @Override
    public void emptyCart() {
        cartDAO.emptyCart();
    }

    @Override
    public void updateCart(Cart... carts) {
        cartDAO.updateCart(carts);
    }


    @Override
    public void deleteCartItem(Cart cart) {
        cartDAO.deleteCartItem(cart);
    }
}
