package com.phuclongappv2.xk.phuclongappver2;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.phuclongappv2.xk.phuclongappver2.Adapter.CartAdapter;
import com.phuclongappv2.xk.phuclongappver2.Database.ModelDB.Cart;
import com.phuclongappv2.xk.phuclongappver2.Interface.RecyclerItemTouchHelperListener;
import com.phuclongappv2.xk.phuclongappver2.Utils.Common;
import com.phuclongappv2.xk.phuclongappver2.Utils.RecyclerItemTouchHelper;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class ActivityCart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    Toolbar toolbar;
    RelativeLayout relativeLayout, existLayout, emptyLayout;
    RecyclerView cartList;
    FButton placeButton;
    TextView total;
    CartAdapter cartAdapter;
    List<Cart> local_listcart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        relativeLayout = findViewById(R.id.cart_layout);
        existLayout = findViewById(R.id.cart_exist_layout);
        emptyLayout = findViewById(R.id.empty_cart_layout);

        cartList = findViewById(R.id.cart_list);
        placeButton = findViewById(R.id.order_button);
        total = findViewById(R.id.total_cart);

        cartList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cartList.setHasFixedSize(true);

        toolbar = findViewById(R.id.tool_bar_cart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(cartList);

        loadCartItem();
    }

    private void loadCartItem() {
        List<Cart> carts = Common.cartRepository.getCartItems();

        if (Common.cartRepository.countCartItem() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            existLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            displayCart(carts);
        }
    }

    private void displayCart(List<Cart> carts) {
        local_listcart = carts;
        cartAdapter = new CartAdapter(this, carts, total);
        cartList.setAdapter(cartAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItem();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RecyclerView.ViewHolder) {
            String name = local_listcart.get(viewHolder.getAdapterPosition()).cName;

            final Cart deleteItem = local_listcart.get(viewHolder.getAdapterPosition());
            final int deleteItemIndex = viewHolder.getAdapterPosition();

            cartAdapter.removeCart(deleteItemIndex);

            cartAdapter.notifyDataSetChanged();

            Common.cartRepository.deleteCartItem(deleteItem);

            Snackbar snackbar = Snackbar.make(relativeLayout, new StringBuilder(name).append(" đã được xóa khỏi giỏ hàng").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartAdapter.restoreCart(deleteItem, deleteItemIndex);
                    Common.cartRepository.insertCart(deleteItem);
                    cartAdapter.notifyDataSetChanged();
                    existLayout.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            if (Common.cartRepository.countCartItem() == 0) {
                total.setText("0 VNĐ");
                emptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
