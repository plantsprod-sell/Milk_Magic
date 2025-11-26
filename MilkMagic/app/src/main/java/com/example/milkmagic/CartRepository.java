package com.example.milkmagic;

import java.util.ArrayList;

public class CartRepository {
    private static final ArrayList<CartModel> cartItems = new ArrayList<>();

    // This flag ensures we only load demo data ONCE per app launch
    public static boolean isDemoLoaded = false;

    public static void addItem(CartModel item) {
        boolean exists = false;
        for (CartModel existing : cartItems) {
            // Using Name as ID. logic
            if (existing.getName().equals(item.getName())) {
                // If item exists, just add quantity
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                exists = true;
                break;
            }
        }
        if (!exists) {
            cartItems.add(item);
        }
    }

    public static ArrayList<CartModel> getCartItems() {
        return cartItems;
    }
}