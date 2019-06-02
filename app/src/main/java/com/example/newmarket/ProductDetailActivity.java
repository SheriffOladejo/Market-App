package com.example.newmarket;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.newmarket.Model.Cart;
import com.example.newmarket.Model.Products;
import com.example.newmarket.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ProductDetailActivity extends AppCompatActivity {

    private Button addToCart;
    private RelativeLayout relativeLayout;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, productName, productDescription, discount;
    private String productId = "";
    private String vendor;
    private String state = "normal";
    private String userPhone = LoginActivity.currentOnlineUser.getPhone();
    public static Cart currentOrderObject;
    private Products products;
    private final HashMap<String, Object> cartMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Paper.init(this);

        relativeLayout = findViewById(R.id.product_details_relative_layout);
        productId = getIntent().getStringExtra("pid");
        numberButton = findViewById(R.id.number_btn);
        productImage = findViewById(R.id.product_image_details);
        productPrice = findViewById(R.id.original_price);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        addToCart = findViewById(R.id.product_add_to_cart_btn);
        discount = findViewById(R.id.discount);
        currentOrderObject = new Cart();

        getProductDetails(productId);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Users");

        cartMap.put("pid", productId);
        cartMap.put("Product Name", productName.getText().toString());
        cartMap.put("Price", productPrice.getText().toString());
        cartMap.put("Quantity", numberButton.getNumber());
        cartMap.put("Discount", "");
        cartMap.put("Vendor", products.getVendor());
        cartMap.put("Buyer_Phone_Number", LoginActivity.currentOnlineUser.getPhone());
        cartMap.put("Buyer_Name", LoginActivity.currentOnlineUser.getFirstname());

        DatabaseReference vendor = FirebaseDatabase.getInstance().getReference().child("Vendors");
        vendor.child(products.getVendor()).child("Orders").updateChildren(cartMap);

        cartListRef.child(userPhone)
        .child("Cart").child(products.getPid())
        .updateChildren(cartMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                useSnackBar("Added to Bucket");
            }
            else{
                useSnackBar("No vex abeg try again");
            }
            }

        });
    }

    private void useSnackBar(String snackBarMessage) {
        Snackbar snackbar = Snackbar.make(relativeLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    products = dataSnapshot.getValue(Products.class);
                    productName.setText(MainActivity.convertFirstLetter(products.getProduct_Name()));
                    productPrice.setText(products.getPrice());
                    productDescription.setText(MainActivity.convertFirstLetter(products.getDescription()));
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        checkOrderState();
//        if(state.equals("Order Placed") || state.equals("Order Shipped")){
//            Toast.makeText(ProductDetailActivity.this, "You can purchase more products once your order is shipped", Toast.LENGTH_LONG).show();
//        }
//        else
//            addingToCartList();
    }

    private void checkOrderState(){
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(LoginActivity.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("State").getValue().toString();
                    if(shippingState.equals("shipped")){
                        state = "Order Shipped";
                    }
                    else if(shippingState.equals("not shipped")){
                        state = "Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
