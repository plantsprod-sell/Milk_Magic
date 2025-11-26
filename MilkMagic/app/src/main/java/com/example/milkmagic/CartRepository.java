package com.example.milkmagic;

import java.util.ArrayList;

public class CartRepository {
    // Keeps the list of items alive as long as the app is running
    private static final ArrayList<CartModel> cartItems = new ArrayList<>();

    public static void addItem(CartModel item) {
        // Prevent adding the same item twice (just for safety)
        for (CartModel existing : cartItems) {
            if (existing.getName().equals(item.getName())) {
                return;
            }
        }
        cartItems.add(item);
    }

    public static ArrayList<CartModel> getCartItems() {
        return cartItems;
    }
}