package com.example.milkmagic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

    @NonNull @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_card, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        VendorModel vendor = vendorList.get(position);
        holder.tvVendorName.setText(vendor.getVendorName());
        holder.tvPrice.setText(vendor.getPrice());

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(vendor.getImageList());
        holder.vpVendorImages.setAdapter(sliderAdapter);
        holder.indicator.setViewPager(holder.vpVendorImages);
    }

    @Override
    public int getItemCount() { return vendorList.size(); }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvPrice;
        ViewPager2 vpVendorImages;
        CircleIndicator3 indicator;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            vpVendorImages = itemView.findViewById(R.id.vp_vendor_images);
            indicator = itemView.findViewById(R.id.indicator_dots);
        }
    }
}