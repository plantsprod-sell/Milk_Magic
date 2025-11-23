package com.example.milkmagic;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // --------------------------
    // JSON Reader
    // --------------------------
    public JSONObject getJsonLimits() {
        try {
            InputStream is = getResources().openRawResource(R.raw.milk_limit);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int c;
            while ((c = is.read()) != -1) buffer.write(c);
            is.close();

            return new JSONObject(buffer.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

        // Read limit JSON
        JSONObject limits = getJsonLimits();
        int maxMilk   = limits.optInt("milk", 5);
        int maxCurd   = limits.optInt("curd", 10);
        int maxCustom = limits.optInt("custom", 2);

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) return true;
                else if (id == R.id.nav_map) {
                    startActivity(new Intent(getApplicationContext(), VendorActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });

        // --------------------------
        // ViewPager + Dots
        // --------------------------
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

        // Setup 3 Independent Cards
        setupMilkQuickCard(maxMilk);
        setupCurdQuickCard(maxCurd);
        setupCustomQuickCard(maxCustom);
    }

    // -----------------------------------------
    // MILK QUICK CARD
    // -----------------------------------------
    private void setupMilkQuickCard(int maxMilk) {

        LinearLayout layoutAdd = findViewById(R.id.layout_add_milk);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_milk);

        TextView btnAdd = findViewById(R.id.btn_add_milk);
        TextView tvCount = findViewById(R.id.tv_count_milk);

        ImageButton plus = findViewById(R.id.btn_plus_milk);
        ImageButton minus = findViewById(R.id.btn_minus_milk);

        final int[] count = {0};

        btnAdd.setOnClickListener(v -> {
            layoutAdd.setVisibility(View.GONE);
            layoutCounter.setVisibility(View.VISIBLE);
            count[0] = 1;
            tvCount.setText("1");
        });

        plus.setOnClickListener(v -> {
            if (count[0] < maxMilk) {
                count[0]++;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                Toast.makeText(this, "Max Milk Limit: " + maxMilk, Toast.LENGTH_SHORT).show();
            }
        });

        minus.setOnClickListener(v -> {
            if (count[0] > 1) {
                count[0]--;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                layoutCounter.setVisibility(View.GONE);
                layoutAdd.setVisibility(View.VISIBLE);
                count[0] = 0;
            }
        });
    }

    // -----------------------------------------
    // CURD QUICK CARD
    // -----------------------------------------
    private void setupCurdQuickCard(int maxCurd) {

        LinearLayout layoutAdd = findViewById(R.id.layout_add_curd);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_curd);

        TextView btnAdd = findViewById(R.id.btn_add_curd);
        TextView tvCount = findViewById(R.id.tv_count_curd);

        ImageButton plus = findViewById(R.id.btn_plus_curd);
        ImageButton minus = findViewById(R.id.btn_minus_curd);

        final int[] count = {0};

        btnAdd.setOnClickListener(v -> {
            layoutAdd.setVisibility(View.GONE);
            layoutCounter.setVisibility(View.VISIBLE);
            count[0] = 1;
            tvCount.setText("1");
        });

        plus.setOnClickListener(v -> {
            if (count[0] < maxCurd) {
                count[0]++;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                Toast.makeText(this, "Max Curd Limit: " + maxCurd, Toast.LENGTH_SHORT).show();
            }
        });

        minus.setOnClickListener(v -> {
            if (count[0] > 1) {
                count[0]--;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                layoutCounter.setVisibility(View.GONE);
                layoutAdd.setVisibility(View.VISIBLE);
                count[0] = 0;
            }
        });
    }

    // -----------------------------------------
    // CUSTOM QUICK CARD
    // -----------------------------------------
    private void setupCustomQuickCard(int maxCustom) {

        LinearLayout layoutAdd = findViewById(R.id.layout_add_custom);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_custom);

        TextView btnAdd = findViewById(R.id.btn_add_custom);
        TextView tvCount = findViewById(R.id.tv_count_custom);

        ImageButton plus = findViewById(R.id.btn_plus_custom);
        ImageButton minus = findViewById(R.id.btn_minus_custom);

        final int[] count = {0};

        btnAdd.setOnClickListener(v -> {
            layoutAdd.setVisibility(View.GONE);
            layoutCounter.setVisibility(View.VISIBLE);
            count[0] = 1;
            tvCount.setText("1");
        });

        plus.setOnClickListener(v -> {
            if (count[0] < maxCustom) {
                count[0]++;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                Toast.makeText(this, "Max Custom Limit: " + maxCustom, Toast.LENGTH_SHORT).show();
            }
        });

        minus.setOnClickListener(v -> {
            if (count[0] > 1) {
                count[0]--;
                tvCount.setText(String.valueOf(count[0]));
            } else {
                layoutCounter.setVisibility(View.GONE);
                layoutAdd.setVisibility(View.VISIBLE);
                count[0] = 0;
            }
        });
    }
}
