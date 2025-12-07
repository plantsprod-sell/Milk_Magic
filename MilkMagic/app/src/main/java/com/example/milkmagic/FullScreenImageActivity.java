package com.example.milkmagic;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Hide Status Bar for Full Screen
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ViewPager2 vpFull = findViewById(R.id.vp_full_screen);
        ImageView btnClose = findViewById(R.id.btn_close);

        // Get Data
        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("IMAGES");
        int position = getIntent().getIntExtra("POSITION", 0);

        // Convert Strings to Models for Adapter
        List<ImageSliderModel> models = new ArrayList<>();
        if(imageUrls != null) {
            for (String url : imageUrls) {
                models.add(new ImageSliderModel(url));
            }
        }

        // Setup Adapter
        ImageSliderAdapter adapter = new ImageSliderAdapter(models);
        vpFull.setAdapter(adapter);
        vpFull.setCurrentItem(position, false); // Jump to clicked image

        btnClose.setOnClickListener(v -> finish());
    }
}