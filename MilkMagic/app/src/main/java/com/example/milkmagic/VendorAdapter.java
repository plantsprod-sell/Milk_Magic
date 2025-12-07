package com.example.milkmagic;

import android.content.Intent;
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

import java.util.ArrayList;
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

        // --- DEBUGGING CLICK LISTENER ---

        // 1. Define the Click Action
        View.OnClickListener openDetailAction = v -> {
            // DEBUG: Show a toast to prove click works
            Toast.makeText(v.getContext(), "Opening: " + vendor.getVendorName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(v.getContext(), VendorDetailActivity.class);
            intent.putExtra("NAME", vendor.getVendorName());
            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                intent.putExtra("IMAGE", vendor.getImageList().get(0).getImageUrl());
            }

            ArrayList<String> imageUrls1 = new ArrayList<>();

            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                for (ImageSliderModel img : vendor.getImageList()) {
                    imageUrls1.add(img.getImageUrl());
                }

                intent.putStringArrayListExtra("IMAGE_LIST", imageUrls1);
            }
            v.getContext().startActivity(intent);
        };
        // 2. Set listener on MULTIPLE views to ensure touch is caught
        holder.itemView.setOnClickListener(openDetailAction);       // The whole card
        holder.tvVendorName.setOnClickListener(openDetailAction);   // The Name Text
        holder.glassOverlay.setOnClickListener(openDetailAction);   // The Glass Bottom

        // Note: We DO NOT set it on ViewPager2 as that breaks swiping.

        // 1. Set Text Data
        holder.tvVendorName.setText(vendor.getVendorName());
        holder.tvPrice.setText(vendor.getPrice());
        holder.tvRating.setText(vendor.getRating());

        // 2. Setup Image Slider
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(vendor.getImageList());
        holder.vpVendorImages.setAdapter(sliderAdapter);
        holder.indicator.setViewPager(holder.vpVendorImages);

        // 3. Handle "Order" Button Click (Show Toast)
        holder.btnOrder.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Added to Cart: " + vendor.getVendorName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(v.getContext(), VendorDetailActivity.class);
            intent.putExtra("NAME", vendor.getVendorName());

            ArrayList<String> imageUrls1 = new ArrayList<>();

            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                for (ImageSliderModel img : vendor.getImageList()) {
                    imageUrls1.add(img.getImageUrl());
                }

                intent.putStringArrayListExtra("IMAGE_LIST", imageUrls1);
            }


            // Pass the first image URL to the detail page
            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                intent.putExtra("IMAGE", vendor.getImageList().get(0).getImageUrl());
            }

            v.getContext().startActivity(intent);
        });

        // ==================================================================
        // 4. HANDLE CARD CLICK -> OPEN DETAIL PAGE (This was likely missing)
        // ==================================================================
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VendorDetailActivity.class);
            intent.putExtra("NAME", vendor.getVendorName());

            ArrayList<String> imageUrls1 = new ArrayList<>();

            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                for (ImageSliderModel img : vendor.getImageList()) {
                    imageUrls1.add(img.getImageUrl());
                }

                intent.putStringArrayListExtra("IMAGE_LIST", imageUrls1);
            }


            // Pass the first image URL to the detail page
            if (vendor.getImageList() != null && !vendor.getImageList().isEmpty()) {
                intent.putExtra("IMAGE", vendor.getImageList().get(0).getImageUrl());
            }

            v.getContext().startActivity(intent);
        });

        // 5. Apply Glass Blur Effect (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (holder.glassOverlay != null) {
                holder.glassOverlay.setRenderEffect(
                        RenderEffect.createBlurEffect(
                                50f, 50f, Shader.TileMode.MIRROR
                        )
                );
            }
        }
    }

    @Override
    public int getItemCount() {
        return vendorList.size();
    }

    // --- View Holder ---
    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvPrice, btnOrder, tvRating;
        ViewPager2 vpVendorImages;
        CircleIndicator3 indicator;
        View glassOverlay;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnOrder = itemView.findViewById(R.id.btn_order);
            tvRating = itemView.findViewById(R.id.tv_rating);
            vpVendorImages = itemView.findViewById(R.id.vp_vendor_images);
            indicator = itemView.findViewById(R.id.indicator_dots);
            glassOverlay = itemView.findViewById(R.id.view_glass_overlay);
        }
    }
}