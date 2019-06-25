package com.example.newmarket;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashSet;

import io.paperdb.Paper;

public class ProductDetailFragment extends Fragment {

    private Button addToCart;
    private LinearLayout relativeLayout;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice, discountTag, vendor, productName, productDescription, discount;
    private String productId = "";
    private String state = "normal";
    private String vendor_phone;
    public static HashSet<String> vendors;
    private EditText extra_details;
    private String userPhone = LoginActivity.currentOnlineUser.getPhone();
    public static Cart currentOrderObject;
    private Products products;
    private int totalPrice = 0;
    public static HashMap<String, Object> cartMap = new HashMap<>();

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        extra_details = view.findViewById(R.id.extra_details);
        relativeLayout = view.findViewById(R.id.product_details_relative_layout);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            productId = bundle.getString("pid");
        }
        Paper.init(getActivity());
        Paper.book().write(String.valueOf(0), totalPrice);
        numberButton = view.findViewById(R.id.number_btn);
        numberButton.setNumber("1");
        numberButton.setRange(1, 10);
        vendors = new HashSet<>();

        productImage = view.findViewById(R.id.product_image_details);
        discountTag = view.findViewById(R.id.textView4);
        productPrice = view.findViewById(R.id.original_price);
        productName = view.findViewById(R.id.product_name_details);
        vendor = view.findViewById(R.id.vendor);
        productDescription = view.findViewById(R.id.product_description_details);
        addToCart = view.findViewById(R.id.product_add_to_cart_btn);
        discount = view.findViewById(R.id.discount);
        currentOrderObject = new Cart();

        getProductDetails(productId);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(numberButton.getNumber() == "0"){
                Toast.makeText(getActivity(), "Select at least one quantity", Toast.LENGTH_SHORT).show();
            }
            else{
                addingToCartList();
            }
            }
        });
        return view;
    }

    private void addingToCartList() {
        String extras = extra_details.getText().toString();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        int price = Integer.valueOf(productPrice.getText().toString());
        int quantity = Integer.valueOf(numberButton.getNumber());
        cartMap.put("pid", productId);
        cartMap.put("Product_Name", productName.getText().toString());
        cartMap.put("Price", productPrice.getText().toString());
        cartMap.put("Quantity", numberButton.getNumber());
        cartMap.put("Discount", products.getDiscount());
        cartMap.put("Vendor", products.getVendor());
        //cartMap.put("Delivery Address", LoginActivity.currentOnlineUser.getAddress());
        //cartMap.put("Buyer_Phone_Number", LoginActivity.currentOnlineUser.getPhone());
        //cartMap.put("Buyer_Name", LoginActivity.currentOnlineUser.getFirstname());
        cartMap.put("Extra_Details", extras);
        vendors.add(products.getVendor_Phone());

        DatabaseReference vendor = FirebaseDatabase.getInstance().getReference().child("Vendors");
        ordersRef.child(LoginActivity.currentOnlineUser.getPhone()).child(products.getPid()).updateChildren(cartMap);
        vendor.child(vendor_phone).child("Orders").updateChildren(cartMap);

        cartListRef.child(userPhone)
            .child("Cart").child(products.getPid())
            .updateChildren(cartMap)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                }
                else{
                    useSnackBar("Error encountered, please try again");
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
                    vendor_phone = products.getVendor_Phone();
                    productName.setText(MainActivity.convertFirstLetter(products.getProduct_Name()));
                    productPrice.setText(products.getPrice());
                    if(products.getDiscount() == "0"){
                        discountTag.setVisibility(View.INVISIBLE);
                    }
                    else
                        discount.setText(products.getDiscount());
                    productDescription.setText(MainActivity.convertFirstLetter(products.getDescription()));
                    vendor.setText(products.getVendor());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
