package com.example.newmarket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.example.newmarket.Model.TotalPrice;
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

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessButton;
    private TextView textTotalAmount, textMsg1;
    private RelativeLayout relativeLayout;
    private int overallTotalPrice = 0;
    private String phone = "";
    private int oneTypeProductTPrice = 0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                //int price = Integer.valueOf(cartListRef);
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
                Paper.init(getActivity());
                holder.textProductQuantity.setText("Quantity: " + model.getQuantity());
                holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
                holder.textProductName.setText(model.getProduct_Name());
                oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overallTotalPrice += oneTypeProductTPrice;
                //individualTotalPrice = Integer.valueOf(model.getTotal_Price());
                //overallTotalPrice += individualTotalPrice;
                //Paper.book().write("total price", overallTotalPrice);
                textTotalAmount.setText("Total Price = #" + overallTotalPrice);
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    DatabaseReference order = FirebaseDatabase.getInstance().getReference().child(LoginActivity.currentOnlineUser.getPhone()).child(model.getPid());
                    order.removeValue();
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
                                Toast.makeText(getActivity(), "Removed from Cart", Toast.LENGTH_SHORT).show();
                            }
                            }
                        });
                    }
                });
                //overallTotalPrice = 0;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        overallTotalPrice = 0;
    }

    private void useSnackBar(String snackBarMessage) {
        Snackbar snackbar = Snackbar.make(relativeLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


}
