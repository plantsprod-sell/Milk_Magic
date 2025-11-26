package com.example.milkmagic;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Status Bar to White and Icons to Dark
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle Bottom Navigation Clicks
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Current Screen - do nothing
                    return true;
                } else if (id == R.id.nav_map) {
                    Toast.makeText(MainActivity.this, "Opening Vendors...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), VendorActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                } else if (id == R.id.nav_cart) {
                    // --- NEW CART LOGIC ---
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    overridePendingTransition(0,0);
                    return true;

                } else if (id == R.id.nav_profile) {
                    Toast.makeText(MainActivity.this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}