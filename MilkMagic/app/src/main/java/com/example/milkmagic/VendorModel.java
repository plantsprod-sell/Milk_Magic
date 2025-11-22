package com.example.milkmagic;

import java.util.List;

public class VendorModel {
    private String vendorName;
    private String price;
    private List<ImageSliderModel> imageList;

    public VendorModel(String vendorName, String price, List<ImageSliderModel> imageList) {
        this.vendorName = vendorName;
        this.price = price;
        this.imageList = imageList;
    }
    public String getVendorName() { return vendorName; }
    public String getPrice() { return price; }
    public List<ImageSliderModel> getImageList() { return imageList; }
}