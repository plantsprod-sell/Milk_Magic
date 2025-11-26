package com.example.milkmagic;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;
import android.os.Build;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // MILK ANIMATION VARS
    private LottieAnimationView lottieMilk; // Renamed for clarity
    private LottieAnimationView lottieCurd;
    private int milkQuantity = 0;
    private int curdQuantity = 0;
    private androidx.cardview.widget.CardView cardViewCart;
    private TextView tvCartCount, tvCartPrice;

    // PRICES (Example prices)
    private  int priceMilk = 0;
    private  int priceCurd = 0;
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
    private void vibratePhone() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            // Vibrate for 50 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                v.vibrate(50);
            }
        }
    }

    // --------------------------
    // Dots Logic
    // --------------------------
    public void addDots(int count, LinearLayout layout) {
        layout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.dot_inactive);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
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

        // 1. Initialize Global Views
        lottieMilk = findViewById(R.id.lottie_milk); // Make sure XML ID is lottie_milk
        lottieCurd = findViewById(R.id.lottie_curd); // Make sure XML ID is lottie_curd

        lottieMilk.setProgress(0f);
        lottieCurd.setProgress(0f);
        // INIT CART BAR
        cardViewCart = findViewById(R.id.card_view_cart);
        tvCartCount = findViewById(R.id.tv_cart_count);
        tvCartPrice = findViewById(R.id.tv_cart_price);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. Read Limits
        JSONObject limits = getJsonLimits();
        JSONObject jsonRoot = getJsonLimits();

        int maxMilk   = (limits != null) ? limits.optInt("milk", 10) : 10;
        int maxCurd   = (limits != null) ? limits.optInt("curd", 10) : 10;
        int maxCustom = (limits != null) ? limits.optInt("custom", 5) : 5;

        // B. Read Prices (NEW LOGIC)
        if (jsonRoot != null) {
            JSONObject priceBlock = jsonRoot.optJSONObject("prices");
            if (priceBlock != null) {
                priceMilk = priceBlock.optInt("milk", 40); // Default 40 if missing
                priceCurd = priceBlock.optInt("curd", 30); // Default 30 if missing
            }
        }

        // 3. Setup Bottom Nav
        setupBottomNav();

        // 4. Setup ViewPager Slider
        setupSlider();

        // 5. SETUP CARDS
        setupMilkQuickCard(maxMilk);
        setupCurdQuickCard(maxCurd);
        setupCustomQuickCard(maxCustom);
    }

    // -----------------------------------------
    // GENERIC ANIMATION LOGIC (FIXED)
    // -----------------------------------------
    // I replaced 'lottieBottle' with 'targetView' everywhere inside this method
    private void animateProduct(LottieAnimationView targetView, float start, float end, boolean isAdding) {
        AnimatorSet animatorSet = new AnimatorSet();

        // CASE 1: ALREADY FULL (Just Shake)
        if (start == 1f && end == 1f && isAdding) {

            // Use targetView, not lottieMilk!
            ObjectAnimator rotate = ObjectAnimator.ofFloat(targetView, "rotation", 0f, -10f, 10f, -5f, 5f, 0f);
            rotate.setDuration(500);
            rotate.setInterpolator(new BounceInterpolator());

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 1.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 1.1f, 1f);
            scaleX.setDuration(300);
            scaleY.setDuration(300);

            animatorSet.playTogether(rotate, scaleX, scaleY);
        }
        // CASE 2: FILLING OR EMPTYING
        else {
            ValueAnimator liquidAnimator = ValueAnimator.ofFloat(start, end);
            liquidAnimator.setDuration(800);
            liquidAnimator.setInterpolator(new DecelerateInterpolator());
            liquidAnimator.addUpdateListener(animation -> {
                // Use targetView!
                targetView.setProgress((float) animation.getAnimatedValue());
            });

            if (end > start) {
                // Use targetView!
                ObjectAnimator rotate = ObjectAnimator.ofFloat(targetView, "rotation", 0f, -10f, 10f, -5f, 5f, 0f);
                rotate.setDuration(800);
                rotate.setInterpolator(new BounceInterpolator());

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 1.1f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 1.1f, 1f);

                animatorSet.playTogether(liquidAnimator, rotate, scaleX, scaleY);
            } else {
                animatorSet.playTogether(liquidAnimator);
            }
        }

        animatorSet.start();
    }

    // -----------------------------------------
    // MILK CARD LOGIC
    // -----------------------------------------
    private void setupMilkQuickCard(int maxLimit) {
        LinearLayout layoutAdd = findViewById(R.id.layout_add_milk);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_milk);
        LinearLayout btnAddClickArea = findViewById(R.id.layout_add_milk);
        TextView tvCount = findViewById(R.id.tv_count_milk);
        ImageButton plus = findViewById(R.id.btn_plus_milk);
        ImageButton minus = findViewById(R.id.btn_minus_milk);

        btnAddClickArea.setOnClickListener(v -> {
            vibratePhone();
            layoutAdd.setVisibility(View.GONE);
            layoutCounter.setVisibility(View.VISIBLE);
            updateMilkQuantity(1, layoutAdd, layoutCounter, tvCount);
        });

        plus.setOnClickListener(v -> {
            vibratePhone();
            if (milkQuantity < maxLimit) {
                updateMilkQuantity(milkQuantity + 1, layoutAdd, layoutCounter, tvCount);
            } else {
                Toast.makeText(this, "Max Limit Reached", Toast.LENGTH_SHORT).show();
            }
        });

        minus.setOnClickListener(v -> {
            vibratePhone();
            if (milkQuantity > 0) {
                updateMilkQuantity(milkQuantity - 1, layoutAdd, layoutCounter, tvCount);
            }
        });
    }

    private void updateMilkQuantity(int newQuantity, View layoutAdd, View layoutCounter, TextView tvCount) {
        float startProgress = lottieMilk.getProgress();
        float endProgress = (newQuantity > 0) ? 1f : 0f;
        boolean isAdding = newQuantity > milkQuantity;

        milkQuantity = newQuantity;
        tvCount.setText(String.valueOf(milkQuantity));

        if (milkQuantity == 0) {
            layoutCounter.setVisibility(View.GONE);
            layoutAdd.setVisibility(View.VISIBLE);
        }

        // Pass lottieMilk here
        animateProduct(lottieMilk, startProgress, endProgress, isAdding);
        updateCartBar();
    }

    // -----------------------------------------
    // CURD CARD LOGIC
    // -----------------------------------------
    private void setupCurdQuickCard(int maxCurd) {
        LinearLayout layoutAdd = findViewById(R.id.layout_add_curd);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_curd);
        LinearLayout btnAddClick = findViewById(R.id.layout_add_curd);
        TextView tvCount = findViewById(R.id.tv_count_curd);
        ImageButton plus = findViewById(R.id.btn_plus_curd);
        ImageButton minus = findViewById(R.id.btn_minus_curd);

        btnAddClick.setOnClickListener(v -> {
            vibratePhone();
            layoutAdd.setVisibility(View.GONE);
            layoutCounter.setVisibility(View.VISIBLE);
            updateCurdQuantity(1, layoutAdd, layoutCounter, tvCount);
        });

        plus.setOnClickListener(v -> {
            vibratePhone();
            if (curdQuantity < maxCurd) {
                updateCurdQuantity(curdQuantity + 1, layoutAdd, layoutCounter, tvCount);
            }
        });

        minus.setOnClickListener(v -> {
            vibratePhone();
            if (curdQuantity > 0) {
                updateCurdQuantity(curdQuantity - 1, layoutAdd, layoutCounter, tvCount);
            }
        });
    }

    private void updateCurdQuantity(int newQuantity, View layoutAdd, View layoutCounter, TextView tvCount) {
        float startProgress = lottieCurd.getProgress();
        float endProgress = (newQuantity > 0) ? 1f : 0f;
        boolean isAdding = newQuantity > curdQuantity;

        curdQuantity = newQuantity;
        tvCount.setText(String.valueOf(curdQuantity));

        if (curdQuantity == 0) {
            layoutCounter.setVisibility(View.GONE);
            layoutAdd.setVisibility(View.VISIBLE);
        }

        // Pass lottieCurd here
        animateProduct(lottieCurd, startProgress, endProgress, isAdding);
        updateCartBar();
    }

    // -----------------------------------------
    // CUSTOM CARD LOGIC (Basic)
    // -----------------------------------------
    private void setupCustomQuickCard(int maxCustom) {
        LinearLayout layoutAdd = findViewById(R.id.layout_add_custom);
        LinearLayout layoutCounter = findViewById(R.id.layout_counter_custom);
        LinearLayout btnAdd = findViewById(R.id.layout_add_custom);
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
            vibratePhone();
            if (count[0] < maxCustom) {
                count[0]++;
                tvCount.setText(String.valueOf(count[0]));
            }
        });

        minus.setOnClickListener(v -> {
            vibratePhone();
            count[0]--;
            if (count[0] <= 0) {
                layoutCounter.setVisibility(View.GONE);
                layoutAdd.setVisibility(View.VISIBLE);
                count[0] = 0;
            } else {
                tvCount.setText(String.valueOf(count[0]));
            }
        });
    }


    private void updateCartBar() {
        int totalItems = milkQuantity + curdQuantity;
       // int totalPrice = (milkQuantity * PRICE_MILK) + (curdQuantity * PRICE_CURD);
        int totalPrice = (milkQuantity * priceMilk) + (curdQuantity * priceCurd);

        if (totalItems > 0) {
            // Show Bar
            if (cardViewCart.getVisibility() == View.GONE) {
                cardViewCart.setVisibility(View.VISIBLE);
                // Optional: Simple Slide Up Animation
                cardViewCart.setAlpha(0f);
                cardViewCart.setTranslationY(100f);
                cardViewCart.animate().alpha(1f).translationY(0f).setDuration(300).start();
            }

            // Update Text
            String itemText = (totalItems == 1) ? " Item" : " Items";
            tvCartCount.setText(totalItems + itemText);
            tvCartPrice.setText("₹" + totalPrice);

        } else {
            // Hide Bar
            cardViewCart.setVisibility(View.GONE);
        }
    }

    // -----------------------------------------
    // Helpers
    // -----------------------------------------
    private void setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_map) {
                startActivity(new Intent(getApplicationContext(), VendorActivity.class));
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }

    private void setupSlider() {
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