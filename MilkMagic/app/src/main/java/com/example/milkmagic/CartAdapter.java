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

/**
 * CartAdapter is the bridge between the data (CartModel list) and the UI (RecyclerView).
 *
 * Responsibilities:
 * 1. Inflates the 'item_cart.xml' layout for each item in the cart.
 * 2. Binds the data (Name, Price, Image) to the views.
 * 3. Handles user interactions:
 * - Clicking (+) increases quantity.
 * - Clicking (-) decreases quantity or removes the item if quantity reaches 0.
 * 4. Notifies the parent Activity via a Runnable callback to recalculate the total bill.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // List of items currently in the cart
    private ArrayList<CartModel> cartList;

    // Callback function to notify CartActivity when the total price needs to change
    private Runnable onCartUpdated;

    /**
     * Constructor
     * @param cartList The list of CartModel objects to display.
     * @param onCartUpdated A Runnable that triggers the calculateBill() method in CartActivity.
     */
    public CartAdapter(ArrayList<CartModel> cartList, Runnable onCartUpdated) {
        this.cartList = cartList;
        this.onCartUpdated = onCartUpdated;
    }

    /**
     * Called when the RecyclerView needs a new row layout.
     * Inflates 'item_cart.xml'.
     */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    /**
     * Called to fill a row with data at a specific position.
     * Contains the core logic for buttons.
     */
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel item = cartList.get(position);

        // --- Bind Data to UI Elements ---
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText("₹" + item.getPrice());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.imgProduct.setImageResource(item.getImageResId());

        // --- Handle PLUS (+) Button Click ---
        holder.btnPlus.setOnClickListener(v -> {
            // 1. Increment quantity in the model
            item.setQuantity(item.getQuantity() + 1);

            // 2. Refresh only this specific row to show new quantity
            notifyItemChanged(position);

            // 3. Trigger callback to update the Total Bill in Activity
            onCartUpdated.run();
        });

        // --- Handle MINUS (-) Button Click ---
        holder.btnMinus.setOnClickListener(v -> {
            int currentQty = item.getQuantity();

            if (currentQty > 1) {
                // Case A: Quantity > 1, just decrease it
                item.setQuantity(currentQty - 1);
                notifyItemChanged(position);
                onCartUpdated.run();
            } else {
                // Case B: Quantity is 1, so clicking minus means REMOVE the item
                cartList.remove(position);

                // 1. Animate the removal
                notifyItemRemoved(position);

                // 2. Refresh index positions for items below the removed one
                // (Critical to prevent crashes when clicking items after deletion)
                notifyItemRangeChanged(position, cartList.size());

                // 3. Update Total Bill (likely to decrease significantly)
                onCartUpdated.run();
            }
        });
    }

    /**
     * Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return cartList.size();
    }

    /**
     * ViewHolder Pattern:
     * Caches references to the views in the layout file to improve scrolling performance.
     * Prevents calling findViewById() repeatedly.
     */
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