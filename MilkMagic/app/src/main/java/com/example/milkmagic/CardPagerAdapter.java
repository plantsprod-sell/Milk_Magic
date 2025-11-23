package com.example.milkmagic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.ViewHolder> {

    private List<CardPageModel> list;
    private Context context;

    public CardPagerAdapter(Context context, List<CardPageModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardPageModel m = list.get(position);

        holder.qty.setText(m.qty);
        holder.product.setText(m.product);
        holder.subtext.setText(m.subtext);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView qty, product, subtext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            qty = itemView.findViewById(R.id.tv_qty);
            product = itemView.findViewById(R.id.tv_product);
            subtext = itemView.findViewById(R.id.tv_subtext);
        }
    }
}
