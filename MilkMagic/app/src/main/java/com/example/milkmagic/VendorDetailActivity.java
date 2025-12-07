package com.example.milkmagic;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class VendorDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_detail);

        // 1. Transparent Status Bar (For Full-Bleed Header)
        // This allows the image to draw behind the battery/time icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // 2. Bind Views
        ViewPager2 vpDetailImages = findViewById(R.id.vp_detail_images);
        CircleIndicator3 indicator = findViewById(R.id.detail_indicator);
        TextView title = findViewById(R.id.tv_detail_name);
        TextView desc = findViewById(R.id.tv_detail_desc);
        ImageView btnBack = findViewById(R.id.btn_back);
        RecyclerView rvProducts = findViewById(R.id.rv_products);

        // 3. Get Data from Intent
        String vendorName = getIntent().getStringExtra("NAME");
        // We receive the simple list of String URLs
        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("IMAGE_LIST");

        // 4. Set Text Data
        if (vendorName != null) {
            title.setText(vendorName);
            desc.setText(vendorName + " offers freshly sourced milk while known for its high quality and taste. Our commitment to providing pure, healthy dairy products makes us the #1 choice for families.");
        }

        // 5. Setup Image Slider (Carousel)
        // We need to convert the String strings back into ImageSliderModels for the adapter
        List<ImageSliderModel> sliderModels = new ArrayList<>();

        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String url : imageUrls) {
                sliderModels.add(new ImageSliderModel(url));
            }
        } else {
            // Fallback Image if none exist
            sliderModels.add(new ImageSliderModel("https://images.unsplash.com/photo-1546445317-29f4545e9d53?ixlib=rb-4.0.3&auto=format&fit=crop&w=1080&q=90"));
        }

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(sliderModels);
        vpDetailImages.setAdapter(sliderAdapter);

        // Link the dots indicator to the ViewPager
        indicator.setViewPager(vpDetailImages);

        // 6. Handle Image Click -> Open Full Screen
        // We need to pass the final list of URLs to the full screen activity
        final ArrayList<String> finalImageUrls = (imageUrls != null && !imageUrls.isEmpty())
                ? imageUrls
                : new ArrayList<>(java.util.Collections.singletonList(sliderModels.get(0).getImageUrl()));

        sliderAdapter.setOnItemClickListener(position -> {
            Intent fullScreenIntent = new Intent(VendorDetailActivity.this, FullScreenImageActivity.class);
            fullScreenIntent.putStringArrayListExtra("IMAGES", finalImageUrls);
            fullScreenIntent.putExtra("POSITION", position);
            startActivity(fullScreenIntent);
        });

        // 7. Setup Product List (Mock Data)
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        List<ProductModel> products = new ArrayList<>();
        products.add(new ProductModel("Cow Milk", "₹60/L"));
        products.add(new ProductModel("Toned Milk", "₹58/L"));
        products.add(new ProductModel("Full Cream Milk", "₹70/L"));
        products.add(new ProductModel("Organic Curd", "₹90/kg"));
        products.add(new ProductModel("Paneer", "₹350/kg"));

        ProductAdapter productAdapter = new ProductAdapter(products);
        rvProducts.setAdapter(productAdapter);

        // 8. Back Button Logic
        btnBack.setOnClickListener(v -> finish());
    }
}