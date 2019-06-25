package com.example.newmarket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;

import com.theartofdev.edmodo.cropper.CropImage;

public class VendorActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        frameLayout = findViewById(R.id.vendor_frameLayout);
        navView = findViewById(R.id.vendor_bottomNavigationView);
        Toolbar toolbar = new Toolbar(VendorActivity.this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Vendor Activity");

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int item = menuItem.getItemId();
            switch(item){
                case R.id.add_new_product:
                    replaceFragment(new AddNewProductFragment());
                    return true;
                case R.id.maintain_products:
                    replaceFragment(new MaintainProductsFragment());
                    return true;
                case R.id.check_new_orders:
                    replaceFragment(new CheckNewOrdersFragment());
                    return true;
                default:
                    return false;
            }
            }
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.vendor_frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vendor_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                AddNewProductFragment.imageUri = result.getUri();
                AddNewProductFragment.InputProductImage.setImageURI(AddNewProductFragment.imageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.vendor_logout:
                startActivity(new Intent(VendorActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
