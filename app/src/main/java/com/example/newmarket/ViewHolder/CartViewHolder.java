package com.example.newmarket.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.newmarket.Interface.ItemClickListener;
import com.example.newmarket.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView textProductName, textProductPrice, textProductQuantity, remove;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        textProductName = itemView.findViewById(R.id.cart_product_name);
        textProductPrice = itemView.findViewById(R.id.cart_product_price);
        textProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        remove = itemView.findViewById(R.id.remove_text);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
