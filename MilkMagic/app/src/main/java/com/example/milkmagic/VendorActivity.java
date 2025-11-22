package com.example.milkmagic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;
import java.util.List;

public class VendorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        // Setup Recycler View
        RecyclerView rvVendors = findViewById(R.id.rv_vendors);
        rvVendors.setLayoutManager(new LinearLayoutManager(this));

        // Create Mock Data
        List<VendorModel> vendorList = new ArrayList<>();
        List<ImageSliderModel> images = new ArrayList<>();
        images.add(new ImageSliderModel("https://upload.wikimedia.org/wikipedia/commons/0/0c/Cow_female_black_white.jpg"));
        images.add(new ImageSliderModel("https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Cow_%28Fleckvieh_breed%29_Oeschinensee_Slaunger_2009-07-07.jpg/640px-Cow_%28Fleckvieh_breed%29_Oeschinensee_Slaunger_2009-07-07.jpg"));

        vendorList.add(new VendorModel("Amul", "₹60/L", images));
        vendorList.add(new VendorModel("Mother Dairy", "₹58/L", images));
        vendorList.add(new VendorModel("Gopal Dairy", "₹65/L", images));

        VendorAdapter adapter = new VendorAdapter(vendorList);
        rvVendors.setAdapter(adapter);

        // --- NAVIGATION LOGIC ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_map); // Highlight "Vendor"

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0); // No animation
                    return true;
                } else if (id == R.id.nav_map) { // "nav_map" is what we used for Vendors in the menu
                    return true;
                }
                return false;
            }
        });
    }
}