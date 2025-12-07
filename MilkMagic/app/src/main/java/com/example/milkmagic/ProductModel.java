package com.example.milkmagic;

public class ProductModel {
    private String name;
    private String price;

    public ProductModel(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
}