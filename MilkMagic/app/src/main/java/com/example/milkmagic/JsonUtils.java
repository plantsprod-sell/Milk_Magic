package com.example.milkmagic;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JsonUtils {

    // A helper class just for reading the file
    public static class RawVendor {
        String name;
        String priceStr;
        public RawVendor(String name, String priceStr) {
            this.name = name;
            this.priceStr = priceStr;
        }
    }

    public static ArrayList<RawVendor> loadRawVendors(Context context) {
        ArrayList<RawVendor> vendors = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("vendors.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("vendorName");
                String price = obj.getString("price");
                vendors.add(new RawVendor(name, price));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vendors;
    }
}