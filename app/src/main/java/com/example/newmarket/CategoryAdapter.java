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

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        ((Item)viewHolder).imageView.setImageResource(images[i]);
    }

    public CategoryAdapter(Context context, String items[], int images[]) {
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
        ImageView imageView;
        CardView cardView;

        public Item(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card);
        }

        @Override
        public void onClick(View v) {
            final Intent intent;
            switch (getAdapterPosition()) {
                case 0:
                    intent = new Intent(v.getContext(), MobilePhonesActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(v.getContext(), LaptopsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(v.getContext(), PhoneLaptopAccessoriesActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(v.getContext(), ClothingsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(v.getContext(), ShoesActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 5:
                    intent = new Intent(v.getContext(), BeautyProductsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 6:
                    intent = new Intent(v.getContext(), BagsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 7:
                    intent = new Intent(v.getContext(), BooksAndStationeriesActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 8:
                    intent = new Intent(v.getContext(), PharmaceuticalsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 9:
                    intent = new Intent(v.getContext(), FoodDrinksActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case 10:
                    intent = new Intent(v.getContext(), ElectronicsActivity.class);
                    v.getContext().startActivity(intent);
                    break;
            }

        }
    }
}
