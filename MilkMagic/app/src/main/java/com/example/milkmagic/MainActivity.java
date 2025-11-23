package com.example.milkmagic;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private int count = 0;
    private TextView tvCount;
    private ImageButton btnPlus;
    private ImageButton btnMinus;
    BottomNavigationView bottomNavigationView;

    // Read max value from res/raw/milk_limit.json
    public int getMaxFromJson() {
        try {
            InputStream is = getResources().openRawResource(R.raw.milk_limit);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int ctr;

            ctr = is.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = is.read();
            }
            is.close();

            String jsonText = byteArrayOutputStream.toString();
            JSONObject jsonObject = new JSONObject(jsonText);

            return jsonObject.getInt("max_quantity");

        } catch (Exception e) {
            e.printStackTrace();
            return 10;  // default max count
        }
    }
    public void addDots(int count, LinearLayout layout) {
        layout.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.dot_inactive);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 0, 8, 0);
            layout.addView(dot, params);
        }
    }

    public void updateDots(int index, LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            ImageView dot = (ImageView) layout.getChildAt(i);
            dot.setImageResource(i == index ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int maxCount = getMaxFromJson();

        tvCount = findViewById(R.id.tv_count);
        btnPlus = findViewById(R.id.btn_plus);
        btnMinus = findViewById(R.id.btn_minus);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_map) {
                    Toast.makeText(MainActivity.this, "Opening Vendor Map...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), VendorActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(MainActivity.this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        // PLUS button (Increase count)
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count < maxCount) {
                    count++;
                    tvCount.setText(String.valueOf(count));
                } else {
                    Toast.makeText(MainActivity.this,
                            "Maximum Limit: " + maxCount,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // MINUS button (Decrease count)
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count > 0) {
                    count--;
                    tvCount.setText(String.valueOf(count));
                }
            }
        });
        ViewPager2 viewPager = findViewById(R.id.viewpager_card);
        LinearLayout dotsContainer = findViewById(R.id.dots_container);

        List<CardPageModel> pages = new ArrayList<>();
        pages.add(new CardPageModel("2 Liters", "Whole Milk", "Daily Subscription"));
        pages.add(new CardPageModel("1 Liter", "Toned Milk", "Next Delivery: Today"));
        pages.add(new CardPageModel("500 ml", "Curd", "Delivered Weekly"));

        CardPagerAdapter adapter = new CardPagerAdapter(this, pages);
        viewPager.setAdapter(adapter);

        addDots(pages.size(), dotsContainer);
        updateDots(0, dotsContainer);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position, dotsContainer);
            }
        });


    }
}
