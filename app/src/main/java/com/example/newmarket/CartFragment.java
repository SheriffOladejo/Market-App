package com.example.newmarket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newmarket.Model.Cart;
import com.example.newmarket.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessButton;
    private TextView textTotalAmount, textMsg1;
    private RelativeLayout relativeLayout;
    private int overallTotalPrice = 0;
    private String phone = "";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        relativeLayout = view.findViewById(R.id.cart_rl);
        recyclerView = view.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        nextProcessButton = view.findViewById(R.id.next_process_btn);
        textTotalAmount = view.findViewById(R.id.total_price);
        textMsg1 = view.findViewById(R.id.msg1);

        nextProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(overallTotalPrice == 0){
                    useSnackBar("No orders have been placed");
                }
                else{
                    Intent intent = new Intent(getActivity(), ConfirmFinalOrder.class);
                    intent.putExtra("Total Price", String.valueOf(overallTotalPrice));
                    startActivity(intent);
                }

            }
        });
        final String userPhone = LoginActivity.currentOnlineUser.getPhone();
        phone = userPhone;
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child(phone)
                        .child("Cart"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.textProductQuantity.setText("Quantity: " + model.getQuantity());
                holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
                holder.textProductName.setText(model.getProduct_Name());

                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference vendor = FirebaseDatabase.getInstance().getReference().child("Vendors");
                        vendor.child(model.getVendor()).child("Orders").child(model.getPid()).removeValue();
                        cartListRef.child(phone)
                                .child("Cart")
                                .child(model.getPid())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            startActivity(new Intent(getActivity(), CartActivity.class));
                                        }
                                    }
                                });
                    }
                });

                int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overallTotalPrice += oneTypeProductTPrice;
                textTotalAmount.setText("Total Price = #" + overallTotalPrice);

//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    CharSequence options[] = new CharSequence[]{
//                        "Edit",
//                        "Remove"
//                    };
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
//                    builder.setTitle("Cart Options");
//
//                    builder.setItems(options, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        if(which == 0){
//                            Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
//                            intent.putExtra("pid", model.getPid());
//                            startActivity(intent);
//                        }
//                        if(which == 1){
//                            cartListRef.child("User View")
//                                .child(Prevalent.currentOnlineUser.getPhone())
//                                .child("Products")
//                                .child(model.getPid())
//                                .removeValue()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Toast.makeText(CartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
//
//                                            Intent intent = new Intent(CartActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }
//                                });
//                        }
//                        }
//                    });
//                    builder.show();
//                    }
//                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_items_layout, viewGroup, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        return view;
    }

    private void useSnackBar(String snackBarMessage) {
        Snackbar snackbar = Snackbar.make(relativeLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
