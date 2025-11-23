package com.example.milkmagic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VendorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        // 1. Theme Status Bar
        getWindow().setStatusBarColor(getResources().getColor(R.color.app_background));
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // 2. Setup Recycler View
        RecyclerView rvVendors = findViewById(R.id.rv_vendors);
        rvVendors.setLayoutManager(new LinearLayoutManager(this));

        // 3. LOAD DATA FROM JSON
        List<VendorModel> vendorList = loadVendorsFromJson();

        // 4. Set Adapter
        VendorAdapter adapter = new VendorAdapter(vendorList);
        rvVendors.setAdapter(adapter);

        // 5. Navigation Logic (Existing code)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_map);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (id == R.id.nav_map) {
                    return true;
                }
                return false;
            }
        });
    }

    // --- Helper Method to Read JSON ---
    private List<VendorModel> loadVendorsFromJson() {
        String json = null;
        try {
            // Open the file from assets folder
            InputStream is = getAssets().open("vendors.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert bytes to String
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>(); // Return empty list on failure
        }

        // Use Gson to convert String to List<VendorModel>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<VendorModel>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}