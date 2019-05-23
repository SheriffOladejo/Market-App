package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.newmarket.Model.Products;
import com.example.newmarket.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference productRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private TextView usernameTextView;
    private String type ="";
    private CircleImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            type = getIntent().getExtras().get("Admin").toString();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.bottom_nav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.user_profile_name);
        profileImageView = headerView.findViewById(R.id.user_profile_image);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        navigationView.setNavigationItemSelectedListener(this);

        if(!type.equals("Admin")){
        if(LoginActivity.currentOnlineUser.getNickname().equals("")){
            usernameTextView.setText(LoginActivity.currentOnlineUser.getFirstname());
        }
        else{
            usernameTextView.setText(LoginActivity.currentOnlineUser.getFirstname());
        }
        }
//        if(!LoginActivity.currentOnlineUser.getImage().equals("")){
//            Picasso.get().load(LoginActivity.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
//        }


//        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Phone");
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    if(dataSnapshot.child("Phone").exists()){
//                        if(dataSnapshot.child("Nickname").getValue().toString().equals("")){
//                            String firstname = dataSnapshot.child("Firstname").getValue().toString();
//                            String imageUri = dataSnapshot.child("Image").getValue().toString();
//                            if(imageUri != ""){
//                                Picasso.get().load(imageUri).into(profileImageView);
//                            }
//                            usernameTextView.setText(firstname);
//                        }
//                        else{
//                            String nickname = dataSnapshot.child("Nickname").getValue().toString();
//                            String imageUri = dataSnapshot.child("Image").getValue().toString();
//                            if(imageUri != ""){
//                                Picasso.get().load(imageUri).into(profileImageView);
//                            }
//                            usernameTextView.setText(nickname);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        //type = getIntent().getExtras().get("Admin").toString();
    }

    // Search data
    private void firebaseSearch(String searchText){
        Query firebaseSearchQuery = productRef.orderByChild("Product_Name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Products> options =
            new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery, Products.class)
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
                        if(type.equals("Admin")){
                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }

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
                    holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
                    Picasso.get().load(model.getImage()).into(holder.imageView);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        if(type.equals("Admin")){
                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search_products);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_categories)
        {
            startActivity(new Intent(MainActivity.this, CategoryActivity.class));
        }
        else if (id == R.id.nav_settings)
        {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout)
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.become_vendor){
            startActivity(new Intent(MainActivity.this, BecomeVendorActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}