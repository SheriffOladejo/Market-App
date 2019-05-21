package com.example.newmarket.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newmarket.Interface.ItemClickListener;
import com.example.newmarket.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView textProductName, textProductDescription, textProductPrice, vendor;
    public ImageView imageView;
    public ItemClickListener listener;

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }

    public ProductViewHolder(View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.product_image);
        textProductDescription = itemView.findViewById(R.id.product_description);
        textProductName = itemView.findViewById(R.id.product_name);
        textProductPrice = itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
}
