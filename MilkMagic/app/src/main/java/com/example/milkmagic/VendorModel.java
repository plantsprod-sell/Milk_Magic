package com.example.milkmagic;

import com.google.gson.annotations.SerializedName; // Import this!
import java.util.ArrayList;
import java.util.List;

public class VendorModel {

    @SerializedName("vendorName") // Force Gson to look for "vendorName"
    private String vendorName;

    @SerializedName("price")
    private String price;

    @SerializedName("rating")
    private String rating;

    @SerializedName("imageUrls") // CRITICAL: matches JSON key "imageUrls"
    private List<String> imageUrls;

    public VendorModel(String vendorName, String price, String rating, List<String> imageUrls) {
        this.vendorName = vendorName;
        this.price = price;
        this.rating = rating;
        this.imageUrls = imageUrls;
    }

    public String getVendorName() { return vendorName; }
    public String getPrice() { return price; }
    public String getRating() { return rating; }

    // Convert raw strings to ImageSliderModels for the Adapter
    public List<ImageSliderModel> getImageList() {
        List<ImageSliderModel> modelList = new ArrayList<>();

        // CHECK IF DATA EXISTS
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                modelList.add(new ImageSliderModel(url));
            }
        } else {
            // If we hit this, JSON parsing FAILED. Show a red flag image.
            modelList.add(new ImageSliderModel("https://via.placeholder.com/600x400/FF0000/FFFFFF?text=JSON+ERROR"));
        }
        return modelList;
    }

    // For passing to Detail Activity
    public ArrayList<String> getImageUrls() {
        return (ArrayList<String>) imageUrls;
    }
}