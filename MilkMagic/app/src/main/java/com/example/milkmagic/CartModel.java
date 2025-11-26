package com.example.milkmagic;

/**
 * CartModel represents a single product item inside the user's shopping cart.
 * * This is a "Plain Old Java Object" (POJO) that holds data such as:
 * - Product Name (e.g., "Amul Milk")
 * - Volume/Size (e.g., "1 Litre")
 * - Price per unit
 * - Quantity selected by the user
 * - Visual representation (Image Resource ID)
 */
public class CartModel {

    // --- Member Variables ---
    private String name;        // The display name of the product
    private String volume;      // The size or weight (e.g., "500ml", "1kg")
    private int price;          // Unit price in Rupees (integer for simplicity)
    private int quantity;       // Current number of items selected
    private int imageResId;     // Local drawable resource ID for the product image

    /**
     * Constructor to initialize a new Cart Item.
     *
     * @param name       The name of the product.
     * @param volume     The volume/size string.
     * @param price      The price per unit.
     * @param quantity   The initial quantity to add.
     * @param imageResId The R.drawable ID for the image.
     */
    public CartModel(String name, String volume, int price, int quantity, int imageResId) {
        this.name = name;
        this.volume = volume;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    // --- Getters (Read Data) ---

    public String getName() {
        return name;
    }

    public String getVolume() {
        return volume;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResId() {
        return imageResId;
    }

    // --- Setters (Write Data) ---

    /**
     * Updates the quantity of this item.
     * This is called when the user clicks (+) or (-) buttons in the CartAdapter.
     *
     * @param quantity The new quantity count.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}