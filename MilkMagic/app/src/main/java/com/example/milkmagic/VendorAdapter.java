package com.example.milkmagic;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private List<VendorModel> vendorList;

    public VendorAdapter(List<VendorModel> vendorList) {
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_card, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        VendorModel vendor = vendorList.get(position);

        // 1. Set Text Data
        holder.tvVendorName.setText(vendor.getVendorName());
        holder.tvPrice.setText(vendor.getPrice());

        // 2. Setup Image Slider (Carousel)
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(vendor.getImageList());
        holder.vpVendorImages.setAdapter(sliderAdapter);
        holder.indicator.setViewPager(holder.vpVendorImages);

        // 3. Handle Order Button Click
        holder.btnOrder.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Ordered from " + vendor.getVendorName(), Toast.LENGTH_SHORT).show();
        });

        // 4. APPLY BLUR EFFECT (Android 12 / API 31+ only)
        // This smooths the gradient overlay to look more like glass
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                // We apply the blur to the overlay view (the gradient)
                // Note: Standard Android APIs cannot easily blur the *background image* behind a view
                // without external libraries. This blurs the gradient itself to make it softer.
                holder.glassOverlay.setRenderEffect(
                        RenderEffect.createBlurEffect(
                                30f, // Blur Radius X
                                30f, // Blur Radius Y
                                Shader.TileMode.MIRROR
                        )
                );
            } catch (Exception e) {
                e.printStackTrace(); // Handle crash on some specific devices
            }
        }
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    // --- ViewHolder Class ---
    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvPrice, btnOrder;
        ViewPager2 vpVendorImages;
        CircleIndicator3 indicator;
        View glassOverlay; // Reference to the gradient view

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnOrder = itemView.findViewById(R.id.btn_order);
            vpVendorImages = itemView.findViewById(R.id.vp_vendor_images);
            indicator = itemView.findViewById(R.id.indicator_dots);

            // Make sure your XML has this ID: android:id="@+id/view_glass_overlay"
            glassOverlay = itemView.findViewById(R.id.view_glass_overlay);
        }
    }
}