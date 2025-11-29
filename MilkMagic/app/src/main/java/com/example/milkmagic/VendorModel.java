package com.example.milkmagic;

import java.util.ArrayList;
import java.util.List;

public class VendorModel {
    // These names must match the JSON keys exactly!
    private String vendorName;
    private String price;
    private String rating;
    private List<String> imageUrls;

    // Constructor
    public VendorModel(String vendorName, String price, List<String> imageUrls) {
        this.vendorName = vendorName;
        this.price = price;
        this.rating = rating;
        this.imageUrls = imageUrls;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getPrice() {
        return price;
    }
    public String getRating() { return rating; }

    // Helper method to convert Strings to ImageSliderModels for the Adapter
    public List<ImageSliderModel> getImageList() {
        List<ImageSliderModel> modelList = new ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                modelList.add(new ImageSliderModel(url));
            }
        }
        return modelList;
    }
}