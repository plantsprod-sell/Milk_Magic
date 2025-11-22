package com.example.milkmagic;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
                    // Logic to open Map/Vendor Activity
                    Toast.makeText(MainActivity.this, "Opening Vendor Map...", Toast.LENGTH_SHORT).show();
                    // Intent intent = new Intent(MainActivity.this, VendorActivity.class);
                    // startActivity(intent);
                    startActivity(new Intent(getApplicationContext(), VendorActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Logic to open Profile Activity
                    Toast.makeText(MainActivity.this, "Opening Profile...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}
