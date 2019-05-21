package com.example.newmarket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {

//    private ImageView tshirts, sportTshirts, glasses, electronics, belts;
//    private ImageView shoes, sweaters, femaledresses;
//    private ImageView purses, mobiles, headphones, watches;

    private Button logoutBtn, checkOrders, maintinProducts;
    private RecyclerView recyclerView;
    private String category = "";
    int images[] = {R.drawable.mobile_phones, R.drawable.laptops, R.drawable.laptops, R.drawable.clothing, R.drawable.shoess, R.drawable.beauty, R.drawable.bags, R.drawable.books, R.drawable.pharmacy, R.drawable.food, R.drawable.electronics, R.drawable.others};
    private String items[] = {"Mobile Phones", "Laptops", "Phone/Laptop Accessories", "Clothing", "Shoes", "Beauty Products", "Bags", "Books and Stationery", "Pharmaceuticals", "Food/Drinks", "Electronics", "Others"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        recyclerView = findViewById(R.id.admin_category_recyclerview);
        final Intent intent = getIntent();
        category = intent.getStringExtra("category");

//        tshirts = findViewById(R.id.t_shirts);
//        sportTshirts = findViewById(R.id.sport_shirts);
//        glasses = findViewById(R.id.glasses);
//        electronics = findViewById(R.id.electronics);
//        belts = findViewById(R.id.belts);
//        shoes = findViewById(R.id.shoes);
//        sweaters = findViewById(R.id.sweaters);
//        femaledresses = findViewById(R.id.female_dresses);
        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrders = findViewById(R.id.admin_check_new_orders);
        //maintainProducts = findViewById(R.id.maintain_btn);
//        purses = findViewById(R.id.purses_bags);
//        mobiles = findViewById(R.id.mobiles);
//        headphones = findViewById(R.id.headphones);
//        watches = findViewById(R.id.watches);
        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrders = findViewById(R.id.admin_check_new_orders);
        maintinProducts = findViewById(R.id.maintain_btn);

        recyclerView = findViewById(R.id.admin_category_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AdminCategoryAdapter(this, items, images));

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        
        maintinProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminMaintainProductsActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        checkOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

//        maintainProducts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
//                intent.putExtra("Admin", "Admin");
//                startActivity(intent);
//            }
//        });

//        tshirts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "T-Shirts");
//                startActivity(intent);
//            }
//        });
//
//        sportTshirts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Sport Items");
//                startActivity(intent);
//            }
//        });
//
//        belts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Beauty");
//                startActivity(intent);
//            }
//        });
//
//        femaledresses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Drugs and Health");
//                startActivity(intent);
//            }
//        });
//
//        glasses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Fashion Accessories");
//                startActivity(intent);
//            }
//        });
//
//        electronics.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Electronics");
//                startActivity(intent);
//            }
//        });
//
//        shoes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Shoes");
//                startActivity(intent);
//            }
//        });
//
//        sweaters.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Books and Stationery");
//                startActivity(intent);
//            }
//        });
//
//        headphones.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Headphones");
//                startActivity(intent);
//            }
//        });
//
//        mobiles.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Mobiles");
//                startActivity(intent);
//            }
//        });
//
//        watches.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Edibles");
//                startActivity(intent);
//            }
//        });
//
//        purses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
//                intent.putExtra("category", "Others");
//                startActivity(intent);
//            }
//        });
    }
}
