package com.example.newmarket;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.FrameLayout;
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
    private FrameLayout frameLayout;
    private Fragment home_fragment;
    private Fragment settingFragment;
    private Fragment categoryFragment;
    private Fragment cart_fragment;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        home_fragment = new HomeFragment();
        settingFragment = new SettingsFragment();
        categoryFragment = new CategoriesFragment();
        cart_fragment = new CartFragment();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(home_fragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.nav_home:
                        replaceFragment(home_fragment);
                        return true;
                    case R.id.nav_cart:
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                        return true;
                    case R.id.nav_settings:
                        replaceFragment(settingFragment);
                        return true;
                    case R.id.nav_categories:
                        replaceFragment(categoryFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        Intent intent = getIntent();
        frameLayout = findViewById(R.id.main_framelayout);
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            type = getIntent().getExtras().get("Admin").toString();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.bottom_nav);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.user_profile_name);
        usernameTextView.setTextColor(getResources().getColor(R.color.brown));
        profileImageView = headerView.findViewById(R.id.user_profile_image);

        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        //recyclerView = findViewById(R.id.recycler_menu);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new GridLayoutManager(MainActivity.this, 2);
//        recyclerView.setLayoutManager(layoutManager);

        navigationView.setNavigationItemSelectedListener(this);

        if(!type.equals("Admin")){
        if(LoginActivity.currentOnlineUser.getNickname().equals("")){
            usernameTextView.setText(LoginActivity.currentOnlineUser.getFirstname());
        }
        else{
            usernameTextView.setText(LoginActivity.currentOnlineUser.getFirstname());
        }
        }
    }

    public static String convertFirstLetter(String name){
        String result = "";
        String indexZero = name.substring(0, 1);
        result = indexZero.toUpperCase();
        for(int x = 1; x < name.length(); x++){
            result += name.charAt(x);
        }
        return result;
    }

    // Search data
    private void firebasesearch(String searchText){
        Query firebaseSearchQuery = productRef.orderByChild("Product_Name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Products> options =
            new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model){
                    holder.textProductName.setText(convertFirstLetter(model.getProduct_Name()));
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

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_framelayout, fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseRecyclerOptions<Products> options =
//        new FirebaseRecyclerOptions.Builder<Products>()
//        .setQuery(productRef, Products.class)
//        .build();
//
//        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
//            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options){
//                @Override
//                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model){
//                    holder.textProductName.setText(model.getProduct_Name());
//                    holder.textProductPrice.setText("Price: " + "#" + model.getPrice());
//                    Picasso.get().load(model.getImage()).into(holder.imageView);
//
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        if(type.equals("Admin")){
//                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
//                            intent.putExtra("pid", model.getPid());
//                            startActivity(intent);
//                        }
//                        else{
//                            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
//                            intent.putExtra("pid", model.getPid());
//                            startActivity(intent);
//                        }
//
//                        }
//                    });
//
//                }
//
//                @NonNull
//                @Override
//                public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
//                    return new ProductViewHolder(view);
//                }
//            };
//            recyclerView.setAdapter(adapter);
//            adapter.startListening();
//    }

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
                firebasesearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebasesearch(newText);
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
            Paper.book().destroy();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if(id == R.id.become_vendor){
            startActivity(new Intent(MainActivity.this, BecomeVendorActivity.class));
        }
        else if(id == R.id.rate){
            try{
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                return true;
            }catch(ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + "com.android.chrome")));
                return true;
            }
        }
        else if(id == R.id.share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareSubject = "Download the Radical Facts App from PlayStore\n";
            String shareBody = "\n" + "http://play.google.com/store/apps/details?id="  + getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            intent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(intent, "Share Using"));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
