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
        holder.tvRating.setText(vendor.getRating());

        // 2. Setup Image Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(vendor.getImageList());
        holder.vpVendorImages.setAdapter(sliderAdapter);
        holder.indicator.setViewPager(holder.vpVendorImages);

        // 3. Handle Order Button Click
        holder.btnOrder.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Ordered from " + vendor.getVendorName(), Toast.LENGTH_SHORT).show();
        });

        // 4. GLASS BLUR EFFECT (Android 12 / API 31+)
        // We access 'holder.glassOverlay' which we defined in the class below
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (holder.glassOverlay != null) {
                holder.glassOverlay.setRenderEffect(
                        RenderEffect.createBlurEffect(
                                50f, // Horizontal Blur
                                50f, // Vertical Blur
                                Shader.TileMode.MIRROR
                        )
                );
            }
        }
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    // ==========================================
    // VIEW HOLDER CLASS (The fix is here)
    // ==========================================
    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvPrice, tvRating, btnOrder;
        ViewPager2 vpVendorImages;
        CircleIndicator3 indicator;

        // We define the Glass View here so 'holder' can find it
        View glassOverlay;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
            btnOrder = itemView.findViewById(R.id.btn_order);
            vpVendorImages = itemView.findViewById(R.id.vp_vendor_images);
            indicator = itemView.findViewById(R.id.indicator_dots);

            // LINK TO XML ID
            glassOverlay = itemView.findViewById(R.id.view_glass_overlay);
        }
    }
}