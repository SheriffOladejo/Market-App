package com.example.newmarket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference productRef;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(LoginActivity.currentOnlineVendor.getPhone()).child("Products");
        recyclerView = findViewById(R.id.maintain_products_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(AdminMaintainProductsActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        toolbar = findViewById(R.id.admin_maintain_products_toolbar);
        toolbar.setTitle("My Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(productRef, Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model){
                        holder.textProductName.setText(model.getProduct_Name());
//                        holder.textProductDescription.setText(model.getDescription());
                        holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
//                        holder.vendor.setText("Vendor: " + model.getVendor());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminMaintainProductsActivity.this, AdminEditProductsActivity.class);
                                intent.putExtra("pid", model.getPid());
                                intent.putExtra("Image", model.getImage());
                                intent.putExtra("Product Name", model.getProduct_Name());
                                intent.putExtra("Price", model.getPrice());
                                intent.putExtra("Discount", model.getDiscount());
                                intent.putExtra("Description", model.getDescription());
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
    }
}
