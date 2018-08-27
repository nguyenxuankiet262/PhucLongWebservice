package com.phuclongappv2.xk.phuclongappver2.Database.DataSource;

import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;

import java.util.List;

public interface ICartDataSource {
    int isCart(int itemId, String userId);
    List<Cart> getCartItems();
    List<Cart> getCartById(int cartItemID);
    List<Cart> getCartByUserId(String userID);
    int countCartItem(String userID);
    void insertCart(Cart... carts);
    void emptyCart();
    void updateCart(Cart... carts);
    void deleteCartItem(Cart cart);
}
