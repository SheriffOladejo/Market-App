package com.example.newmarket;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoriesFragment extends Fragment {
    private ListView recyclerView;
    String[] items = {"Mobile Phones", "Laptops", "Phone/Laptop Accessories", "Clothing", "Shoes", "Beauty Products", "Bags", "Books and Stationery", "Pharmaceuticals", "Food/Drinks", "Electronics", "Others"};
    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.custom_row, items);

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = (String)recyclerView.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                Fragment fragment = new CategoryProduct();
                fragment.setArguments(bundle);
                replaceFragment(fragment);
            }
        });
        return view;
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_framelayout, fragment);
        fragmentTransaction.commit();
    }
}
