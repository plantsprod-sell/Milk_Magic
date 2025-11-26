package com.example.milkmagic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private ArrayList<CartModel> cartList;
    private Runnable onCartUpdated;

    public CartAdapter(ArrayList<CartModel> cartList, Runnable onCartUpdated) {
        this.cartList = cartList;
        this.onCartUpdated = onCartUpdated;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel item = cartList.get(position);

        holder.txtName.setText(item.getName());
        holder.txtPrice.setText("₹" + item.getPrice());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.imgProduct.setImageResource(item.getImageResId());

        // PLUS BUTTON Logic
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            onCartUpdated.run();
        });

        // MINUS BUTTON Logic (Fixed to remove item at 0)
        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();

            if (currentQty > 1) {
                // Just decrease
                item.setQuantity(currentQty - 1);
                notifyItemChanged(position);
                onCartUpdated.run();
            } else {
                // Quantity is 1, so remove the item
                cartList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartList.size()); // Fix list indexing
                onCartUpdated.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice, txtQuantity;
        ImageButton btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}