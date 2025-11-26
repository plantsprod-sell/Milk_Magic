package com.example.milkmagic;

public class CartModel {
    private String name;
    private String volume;
    private int price;
    private int quantity;
    private int imageResId;

    public CartModel(String name, String volume, int price, int quantity, int imageResId) {
        this.name = name;
        this.volume = volume;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getVolume() { return volume; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}