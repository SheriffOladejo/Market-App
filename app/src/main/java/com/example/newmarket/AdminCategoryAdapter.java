package com.example.newmarket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AdminCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    String items[];
    int images[];

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.custom_row, viewGroup, false);
        Item item = new Item(row);
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((Item) viewHolder).textView.setText(items[i]);
    }

    public AdminCategoryAdapter(Context context, String items[], int images[]) {
        this.context = context;
        this.items = items;
        this.images = images;
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public class Item extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        CardView cardView;
        ImageView imageView;

        public Item(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.image);
            //cardView = itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View v) {
            final Intent intent;
            switch (getAdapterPosition()) {
                case 0:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Mobile Phones");
                    v.getContext().startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Laptops");
                    v.getContext().startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Phone and Laptop Accessories");
                    v.getContext().startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Clothing");
                    v.getContext().startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Shoes");
                    v.getContext().startActivity(intent);
                    break;
                case 5:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Beauty Products");
                    v.getContext().startActivity(intent);
                    break;
                case 6:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Bags");
                    v.getContext().startActivity(intent);
                    break;
                case 7:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Books and Stationery");
                    v.getContext().startActivity(intent);
                    break;
                case 8:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Pharmaceuticals");
                    v.getContext().startActivity(intent);
                    break;
                case 9:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Food and Drinks");
                    v.getContext().startActivity(intent);
                    break;
                case 10:
                    intent = new Intent(v.getContext(), AdminAddNewProductActivity.class);
                    intent.putExtra("category", "Electronics");
                    v.getContext().startActivity(intent);
                    break;
            }

        }
    }
}

