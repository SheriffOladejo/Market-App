package com.example.newmarket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CategoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    String items[] = {"Mobile Phones", "Laptops", "Phone/Laptop Accessories", "Clothing", "Shoes", "Beauty Products", "Bags", "Books and Stationery", "Pharmaceuticals", "Food/Drinks", "Electronics", "Others"};
    int images[] = {R.drawable.mobile_phones, R.drawable.laptops, R.drawable.laptops, R.drawable.clothing, R.drawable.shoess, R.drawable.beauty, R.drawable.bags, R.drawable.books, R.drawable.pharmacy, R.drawable.food, R.drawable.electronics, R.drawable.others};


    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new CategoryAdapter(getActivity(), items, images));
        return view;
    }

}
