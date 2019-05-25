package com.example.newmarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newmarket.Model.Cart;
import com.example.newmarket.Prevalent.Prevalent;
import com.example.newmarket.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessButton;
    private TextView textTotalAmount, textMsg1;
    private int overallTotalPrice = 0;
    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Paper.init(this);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessButton = findViewById(R.id.next_process_btn);
        textTotalAmount = findViewById(R.id.total_price);
        textMsg1 = findViewById(R.id.msg1);

        nextProcessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(overallTotalPrice == 0){
                Toast.makeText(CartActivity.this, "No orders have been placed", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrder.class);
                intent.putExtra("Total Price", String.valueOf(overallTotalPrice));
                startActivity(intent);
            }

            }
        });
        final String userPhone = LoginActivity.currentOnlineUser.getPhone();
        phone = userPhone;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //checkOrderState();

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
                                startActivity(new Intent(CartActivity.this, CartActivity.class));
                                finish();
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
    }

    private void checkOrderState(){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(phone);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("State").getValue().toString();
                    String userName = dataSnapshot.child("Firstname").getValue().toString();
                    if(shippingState.equals("shipped")){
                        textTotalAmount.setText("Dear " + userName + "\n order has been shipped successfully");
                        recyclerView.setVisibility(View.GONE);
                        textMsg1.setVisibility(View.VISIBLE);
                        textMsg1.setText("Congratulations, your final order has been shipped. Soon it will be delivered.");
                        nextProcessButton.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products once you've received your final order", Toast.LENGTH_SHORT).show();
                    }
                    else if(shippingState.equals("not shipped")){
                        textTotalAmount.setText("Shipping State: Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        textMsg1.setVisibility(View.VISIBLE);
                        textMsg1.setText("Your order will be received before 24 hours");
                        nextProcessButton.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "You can purchase more products once you've received your final order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
