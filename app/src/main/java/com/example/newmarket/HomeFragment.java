package com.example.newmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newmarket.Model.Products;
import com.example.newmarket.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference productRef;
    private RecyclerView.LayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.home_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(productRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model){
                        holder.textProductName.setText(model.getProduct_Name());
                        holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                if(type.equals("Admin")){
//                                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
//                                    intent.putExtra("pid", model.getPid());
//                                    startActivity(intent);
//                                }

                                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        return new ProductViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        return view;
    }



//    @Override
//    private void onStart() {
//        super.onStart();
//        FirebaseRecyclerOptions<Products> options =
//                new FirebaseRecyclerOptions.Builder<Products>()
//                        .setQuery(productRef, Products.class)
//                        .build();
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
//                    @Override
//                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model){
//                        holder.textProductName.setText(model.getProduct_Name());
//                        holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
//                        Picasso.get().load(model.getImage()).into(holder.imageView);
//
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if(type.equals("Admin")){
//                                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
//                                    intent.putExtra("pid", model.getPid());
//                                    startActivity(intent);
//                                }
//                                else{
//                                    Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
//                                    intent.putExtra("pid", model.getPid());
//                                    startActivity(intent);
//                                }
//
//                            }
//                        });
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
//                        return new ProductViewHolder(view);
//                    }
//                };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }
}
