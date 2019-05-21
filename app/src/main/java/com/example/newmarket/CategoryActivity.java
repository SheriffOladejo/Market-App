package com.example.newmarket;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    String items[] = {"Mobile Phones", "Laptops", "Phone/Laptop Accessories", "Clothing", "Shoes", "Beauty Products", "Bags", "Books and Stationery", "Pharmaceuticals", "Food/Drinks", "Electronics", "Others"};
    int images[] = {R.drawable.mobile_phones, R.drawable.laptops, R.drawable.laptops, R.drawable.clothing, R.drawable.shoess, R.drawable.beauty, R.drawable.bags, R.drawable.books, R.drawable.pharmacy, R.drawable.food, R.drawable.electronics, R.drawable.others};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryAdapter(this, items, images));
    }

}
