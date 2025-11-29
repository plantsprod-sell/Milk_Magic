package com.example.milkmagic;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView; // Import Lottie
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {
    private List<ImageSliderModel> imageList;

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
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // 1. Show Animation initially
        holder.lottieLoader.setVisibility(View.VISIBLE);
        holder.lottieLoader.playAnimation();

        Glide.with(holder.itemView.getContext())
                .load(imageList.get(position).getImageUrl())
                .apply(new RequestOptions()
                        .format(DecodeFormat.PREFER_ARGB_8888) // 1. FORCE 32-BIT COLOR (Best Quality)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // 2. Cache the High Res image
                        .override(1080, 800)) // 3. Optimize size for CardView
                .transition(DrawableTransitionOptions.withCrossFade()) // 4. Smooth Fade In
                // --- TEST CODE START: Disable Cache to see animation every time ---
                .skipMemoryCache(true)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                // --- TEST CODE END ---
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Optional: Keep animation or show error icon
                        holder.lottieLoader.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 2. Image Loaded! Hide the Cow Animation
                        holder.lottieLoader.setVisibility(View.GONE);
                        holder.lottieLoader.cancelAnimation();
                        return false;
                    }
                })
                .centerCrop()
                .into(holder.sliderImage);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView sliderImage;
        LottieAnimationView lottieLoader; // Define Lottie View

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderImage = itemView.findViewById(R.id.slider_image);
            // Link to XML ID
            lottieLoader = itemView.findViewById(R.id.lottie_loader);
        }
    }
}