package com.example.milkmagic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * CartActivity handles the user's shopping cart logic.
 * * Responsibilities:
 * 1. Display items added from the Vendor page.
 * 2. Manage quantity updates and bill calculations.
 * 3. Handle Delivery Address selection, creation, and deletion.
 * 4. Process payments via COD or UPI Intents.
 * 5. Force Light Mode UI for consistent branding.
 */
public class CartActivity extends AppCompatActivity {

    private CartAdapter cartAdapter;
    private ArrayList<CartModel> cartList;

    // UI Variables
    private TextView txtToPay, txtItemTotal, txtAddress, txtDeliveryFee, txtHandlingFee;
    private Button btnPay;
    private RadioGroup paymentGroup;
    private LinearLayout addressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- UI Configuration ---
        // Force Light Mode to ensure UI consistency (Royal Blue/White theme) regardless of system settings.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_cart);

        // --- Initialize Views ---
        txtToPay = findViewById(R.id.txtToPay);
        txtItemTotal = findViewById(R.id.txtItemTotal);
        txtDeliveryFee = findViewById(R.id.txtDeliveryFee);
        txtHandlingFee = findViewById(R.id.txtHandlingFee);
        btnPay = findViewById(R.id.btnPay);
        paymentGroup = findViewById(R.id.paymentGroup);
        addressLayout = findViewById(R.id.addressLayout);

        // Locate Address TextView (Fallback logic if ID is missing in layout variants)
        try {
            txtAddress = findViewById(R.id.txtCurrentAddress);
            if (txtAddress == null) {
                txtAddress = (TextView) addressLayout.getChildAt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load the last selected address from persistent storage
        loadLastAddress();

        RecyclerView recyclerCart = findViewById(R.id.recyclerCart);

        // --- Demo Data Loader ---
        // Loads mock data from JSON only if the global cart is empty and hasn't been loaded before.
        // Prevents duplicate data on re-entry.
        if (!CartRepository.isDemoLoaded && CartRepository.getCartItems().isEmpty()) {
            ArrayList<JsonUtils.RawVendor> vendors = JsonUtils.loadRawVendors(this);
            // Add first 3 items as default cart content
            for (int i = 0; i < Math.min(vendors.size(), 3); i++) {
                JsonUtils.RawVendor v = vendors.get(i);
                int price = 50;
                try {
                    // Sanitize price string (remove symbols)
                    String clean = v.priceStr.replaceAll("[^0-9]", "");
                    price = Integer.parseInt(clean);
                } catch (Exception e) {}
                CartRepository.addItem(new CartModel(v.name, "1 Litre", price, 1, R.drawable.ic_milk));
            }
            CartRepository.isDemoLoaded = true;
        }

        // --- RecyclerView Setup ---
        cartList = CartRepository.getCartItems();
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));

        // Pass callback to recalculate bill whenever Adapter changes quantities
        cartAdapter = new CartAdapter(cartList, new Runnable() {
            @Override
            public void run() {
                calculateBill();
            }
        });
        recyclerCart.setAdapter(cartAdapter);

        // --- Event Listeners ---

        // Recalculate total when payment method changes
        paymentGroup.setOnCheckedChangeListener((group, checkedId) -> calculateBill());

        // Address Selection Logic
        addressLayout.setOnClickListener(v -> showAddressSelectionDialog());
        findViewById(R.id.btnChangeAddress).setOnClickListener(v -> showAddressSelectionDialog());

        // Navigate to Vendor Activity to add more items
        findViewById(R.id.btnAddMore).setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VendorActivity.class);
            startActivity(intent);
        });

        // Payment Logic
        btnPay.setOnClickListener(v -> {
            int selectedId = paymentGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.radioCOD) {
                Toast.makeText(CartActivity.this, "Order Placed via COD!", Toast.LENGTH_LONG).show();
            } else if (selectedId == R.id.radioUPI) {
                // Initiate UPI Payment Intent
                String amount = txtToPay.getText().toString().replace("₹", "").trim();

                // Configure UPI Deep Link
                String payeeAddress = "91912@ybl"; // Merchant VPA
                String payeeName = "Milk Magic Store"; // Merchant Name

                Uri uri = Uri.parse("upi://pay").buildUpon()
                        .appendQueryParameter("pa", payeeAddress)
                        .appendQueryParameter("pn", payeeName)
                        .appendQueryParameter("tn", "Milk Order Payment")
                        .appendQueryParameter("am", amount)
                        .appendQueryParameter("cu", "INR")
                        .build();

                Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                upiPayIntent.setData(uri);

                // Launch App Chooser (GPay, PhonePe, Paytm, etc.)
                Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                if (upiPayIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(CartActivity.this, "No UPI app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Back Navigation
        findViewById(R.id.header).setOnClickListener(v -> finish());

        // Initial Calculation
        calculateBill();
    }

    /**
     * Called when returning to this activity (e.g., from Vendor page).
     * Refreshes the cart list to show newly added items.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
            calculateBill();
        }
    }

    // =================================================================================
    // ADDRESS MANAGEMENT SYSTEM
    // =================================================================================

    /**
     * Displays a dialog listing saved addresses and an option to add a new one.
     */
    private void showAddressSelectionDialog() {
        List<String> addresses = getSavedAddresses();
        final String[] options = new String[addresses.size() + 1];
        options[0] = "+ Add New Address"; // First option

        // Populate saved addresses
        for (int i = 0; i < addresses.size(); i++) {
            options[i + 1] = addresses.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Delivery Address");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // User clicked "Add New Address"
                showAddNewAddressDialog();
            } else {
                // User clicked an existing address -> Show Actions (Select/Delete)
                String selectedAddress = options[which];
                int addressIndex = which - 1; // Adjust index offset
                showAddressActionDialog(selectedAddress, addressIndex);
            }
        });
        builder.show();
    }

    /**
     * Shows a dialog to either Select the address for delivery or Delete it.
     */
    private void showAddressActionDialog(String address, int index) {
        String[] actions = {"Deliver Here", "Delete Address"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Address");
        builder.setItems(actions, (dialog, which) -> {
            if (which == 0) {
                // Action: Select Address
                txtAddress.setText(address);
                saveLastUsedAddress(address);
            } else {
                // Action: Delete Address
                deleteAddress(index);
            }
        });
        builder.show();
    }

    /**
     * Removes an address from SharedPreferences and updates the UI.
     */
    private void deleteAddress(int index) {
        List<String> addresses = getSavedAddresses();
        if (index >= 0 && index < addresses.size()) {
            String removed = addresses.remove(index);

            // Update Storage
            SharedPreferences prefs = getSharedPreferences("MilkMagicData", MODE_PRIVATE);
            prefs.edit().putString("saved_addresses", new Gson().toJson(addresses)).apply();

            // If the deleted address was currently selected, reset the UI
            if (txtAddress.getText().toString().equals(removed)) {
                txtAddress.setText("Select Address");
                saveLastUsedAddress("Select Address");
            }

            Toast.makeText(this, "Address Deleted", Toast.LENGTH_SHORT).show();
            // Re-open dialog to reflect changes immediately
            showAddressSelectionDialog();
        }
    }

    /**
     * Dialog to input a new address manually.
     */
    private void showAddNewAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Address");
        final EditText input = new EditText(this);
        input.setHint("e.g. Home: #123, 4th Cross, Bengaluru");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newAddress = input.getText().toString();
            if (!newAddress.isEmpty()) {
                saveNewAddress(newAddress);
                txtAddress.setText(newAddress);
                saveLastUsedAddress(newAddress);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- SharedPreferences Helpers (Persistence) ---

    private void saveNewAddress(String address) {
        List<String> addresses = getSavedAddresses();
        addresses.add(0, address); // Add to top of list
        SharedPreferences prefs = getSharedPreferences("MilkMagicData", MODE_PRIVATE);
        prefs.edit().putString("saved_addresses", new Gson().toJson(addresses)).apply();
    }

    private List<String> getSavedAddresses() {
        SharedPreferences prefs = getSharedPreferences("MilkMagicData", MODE_PRIVATE);
        String json = prefs.getString("saved_addresses", null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private void saveLastUsedAddress(String address) {
        getSharedPreferences("MilkMagicData", MODE_PRIVATE).edit().putString("last_address", address).apply();
    }

    private void loadLastAddress() {
        String last = getSharedPreferences("MilkMagicData", MODE_PRIVATE).getString("last_address", "H. No. 123, 1st Floor, Bengaluru");
        if (txtAddress != null) txtAddress.setText(last);
    }

    // =================================================================================
    // BILLING LOGIC
    // =================================================================================

    /**
     * Calculates the bill based on cart items.
     * Logic:
     * - Sums item prices.
     * - Applies Free Delivery logic (Blue text).
     * - Updates Pay Button state (Disabled if empty).
     */
    private void calculateBill() {
        int itemTotal = 0;
        for (CartModel item : cartList) {
            itemTotal += (item.getPrice() * item.getQuantity());
        }

        if (txtItemTotal != null) txtItemTotal.setText("₹" + itemTotal);

        // --- Business Logic: Fees are FREE for now ---
        // Uses HTML to render strikethrough and colored text
        if (txtDeliveryFee != null) {
            if (itemTotal > 0) {
                txtDeliveryFee.setText(Html.fromHtml("<s>₹30</s> <font color='#039BE5'>FREE</font>"));
                txtHandlingFee.setText(Html.fromHtml("<s>₹10</s> <font color='#039BE5'>FREE</font>"));
            } else {
                txtDeliveryFee.setText("₹0");
                txtHandlingFee.setText("₹0");
            }
        }

        // Calculate Total (Currently fees are 0)
        int totalToPay = itemTotal;
        if (txtToPay != null) txtToPay.setText("₹" + totalToPay);

        // Update Pay Button UI/UX
        if (btnPay != null) {
            if (itemTotal == 0) {
                // Case 1: Empty Cart -> Disable Button & Grey Out
                btnPay.setText("Cart is Empty");
                btnPay.setEnabled(false);
                btnPay.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                // Case 2: Has Items -> Enable Button & Show Amount
                btnPay.setEnabled(true);
                btnPay.setBackgroundColor(getResources().getColor(R.color.btn_blue_bg));

                String mode = (paymentGroup.getCheckedRadioButtonId() == R.id.radioCOD) ? "(COD)" : "(UPI)";
                btnPay.setText("Pay " + mode + " ₹" + totalToPay);
            }
        }
    }
}