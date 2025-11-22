package com.example.milkmagic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {
    private List<ImageSliderModel> imageList;

    public ImageSliderAdapter(List<ImageSliderModel> imageList) {
        this.imageList = imageList;
    }

    @NonNull @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(imageList.get(position).getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery) // Shows while loading
                .error(android.R.drawable.ic_menu_report_image)   // Shows if URL fails
                .centerCrop()
                .into(holder.sliderImage);
    }

    @Override
    public int getItemCount() { return imageList.size(); }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView sliderImage;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.slider_image);
        }
    }
}