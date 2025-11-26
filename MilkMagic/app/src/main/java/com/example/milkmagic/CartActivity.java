package com.example.milkmagic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private CartAdapter cartAdapter;
    private ArrayList<CartModel> cartList;

    // UI Variables
    private TextView txtToPay, txtItemTotal;
    private Button btnPay;
    private RadioGroup paymentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 1. Initialize Views
        txtToPay = findViewById(R.id.txtToPay);
        txtItemTotal = findViewById(R.id.txtItemTotal);
        btnPay = findViewById(R.id.btnPay);
        paymentGroup = findViewById(R.id.paymentGroup);
        RecyclerView recyclerCart = findViewById(R.id.recyclerCart);

        // 2. DEMO LOADER
        if (CartRepository.getCartItems().isEmpty()) {
            ArrayList<JsonUtils.RawVendor> vendors = JsonUtils.loadRawVendors(this);
            for (int i = 0; i < Math.min(vendors.size(), 3); i++) {
                JsonUtils.RawVendor v = vendors.get(i);
                int price = 50;
                try {
                    String clean = v.priceStr.replaceAll("[^0-9]", "");
                    price = Integer.parseInt(clean);
                } catch (Exception e) {}
                CartRepository.addItem(new CartModel(v.name, "1 Litre", price, 1, R.drawable.ic_milk));
            }
        }

        // 3. Setup RecyclerView
        cartList = CartRepository.getCartItems();
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(cartList, new Runnable() {
            @Override
            public void run() {
                calculateBill();
            }
        });
        recyclerCart.setAdapter(cartAdapter);

        // 4. Payment Selection Logic
        paymentGroup.setOnCheckedChangeListener((group, checkedId) -> calculateBill());

        // --- FIX 1: ADDRESS CLICK (Open Maps) ---
        findViewById(R.id.addressLayout).setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=H. No. 123, 1st Floor, Indira Nagar, Bengaluru");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                // If maps not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Bengaluru")));
            }
        });

        // --- FIX 2: ADD MORE ITEMS (Go to Vendors) ---
        findViewById(R.id.btnAddMore).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VendorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears cart from stack
            startActivity(intent);
            finish();
        });

        // --- FIX 3: PAY BUTTON (UPI LOGIC) ---
        btnPay.setOnClickListener(v -> {
            int selectedId = paymentGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.radioCOD) {
                Toast.makeText(CartActivity.this, "Order Placed via COD!", Toast.LENGTH_LONG).show();
            } else if (selectedId == R.id.radioUPI) {
                // Prepare UPI Intent
                String amount = txtToPay.getText().toString().replace("₹", "").trim();

                Uri uri = Uri.parse("upi://pay").buildUpon()
                        .appendQueryParameter("pa", "merchant@upi") // Replace with real ID later
                        .appendQueryParameter("pn", "Milk Magic")
                        .appendQueryParameter("tn", "Milk Order")
                        .appendQueryParameter("am", amount)
                        .appendQueryParameter("cu", "INR")
                        .build();

                Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                upiPayIntent.setData(uri);

                // Show App Chooser
                Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                if (upiPayIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(CartActivity.this, "No UPI app found on this phone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 5. Back Button Logic
        findViewById(R.id.header).setOnClickListener(v -> finish());

        // Initial Calculation
        calculateBill();
    }

    private void calculateBill() {
        int itemTotal = 0;
        for (CartModel item : cartList) {
            itemTotal += (item.getPrice() * item.getQuantity());
        }
        int deliveryFee = 30;
        int totalToPay = itemTotal + deliveryFee;

        if (txtItemTotal != null) txtItemTotal.setText("₹" + itemTotal);
        if (txtToPay != null) txtToPay.setText("₹" + totalToPay);

        if (btnPay != null) {
            String mode = (paymentGroup.getCheckedRadioButtonId() == R.id.radioCOD) ? "(COD)" : "(UPI)";
            btnPay.setText("Pay " + mode + " ₹" + totalToPay);
        }
    }
}