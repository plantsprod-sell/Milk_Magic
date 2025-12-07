package com.example.milkmagic;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {
    private List<ImageSliderModel> imageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageSliderAdapter(List<ImageSliderModel> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 1. FORCE ANIMATION START: Make sure it's visible before image loads
        holder.lottieLoader.setVisibility(View.VISIBLE);
        holder.lottieLoader.playAnimation();

        // 2. Clear the previous image (prevents flickering old images)
        holder.sliderImage.setImageDrawable(null);

        // 3. Load Image
        Glide.with(holder.itemView.getContext())
                .load(imageList.get(position).getImageUrl())
                // --- DEBUG: Disable cache to see animation every time (Remove for production) ---
                .skipMemoryCache(true)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                // -------------------------------------------------------------------------------
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Hide animation on failure
                        holder.lottieLoader.cancelAnimation();
                        holder.lottieLoader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // SUCCESS: Hide animation
                        holder.lottieLoader.cancelAnimation();
                        holder.lottieLoader.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.sliderImage);

        // 4. Handle Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView sliderImage;
        LottieAnimationView lottieLoader;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.slider_image);
            lottieLoader = itemView.findViewById(R.id.lottie_loader);
        }
    }
}