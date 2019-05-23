package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminEditProductsActivity extends AppCompatActivity {

    private String productID, downloadImageUrl, Description, productRandomKey, Price, ProductName, saveCurrentDate, saveCurrentTime, categoryName;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDesc, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private Button AddNewProductButton;
    private StorageReference productImagesRef;
    private StorageReference filePath;
    private DatabaseReference productRef, productCategoryRef, vendorProductRef;
    private ProgressDialog progress;
    private HashMap<String, Object> productMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_products);

        AddNewProductButton = findViewById(R.id.apply_changes);
        InputProductName = findViewById(R.id.product_name_edit);
        InputProductDesc = findViewById(R.id.product_description_edit);
        InputProductPrice = findViewById(R.id.product_price_edit);
        InputProductImage = findViewById(R.id.product_image_edit);
        progress = new ProgressDialog(this);

        productID = getIntent().getStringExtra("pid");

        productCategoryRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        productCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    categoryName = dataSnapshot.child("Category").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        vendorProductRef = FirebaseDatabase.getInstance().getReference().child("Admins").child(LoginActivity.currentOnlineVendor.getPhone()).child("Products").child(productID);

        String image = getIntent().getStringExtra("Image");
        Picasso.get().load(image).into(InputProductImage);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();

            }
        });


        productCategoryRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

    }

    private void validateProductData() {
        Description = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString();

        if(imageUri == null){
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
            Toast.makeText(this, "Product description is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(ProductName))
            Toast.makeText(this, "Product name is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(Price))
            Toast.makeText(this, "Product price is mandatory", Toast.LENGTH_SHORT).show();
        else{
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {
        progress.setTitle("Updating Product.");
        progress.setMessage("Dear Admin, please wait while we update your product.");
        progress.setCancelable(false);
        progress.show();
        filePath = productImagesRef.child(categoryName).child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast.makeText(AdminEditProductsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                progress.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminEditProductsActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminEditProductsActivity.this, "Got product image successfully", Toast.LENGTH_SHORT).show();
                            updateProductInfoToProductRef();
                        }
                    }
                });
            }
        });
    }

    private void updateProductInfoToProductRef(){
        productMap = new HashMap<>();
        productMap.put("Price", Price);
        productMap.put("Description", Description);
        productMap.put("Image", downloadImageUrl);
        productMap.put("Product_Name", ProductName);

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateProductToVendorRef();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(AdminEditProductsActivity.this, "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void updateProductToVendorRef(){
        vendorProductRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateProductToCategoryRef();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(AdminEditProductsActivity.this, "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void updateProductToCategoryRef(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(categoryName).child(productID);
        ref.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AdminEditProductsActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }
                else{
                    Toast.makeText(AdminEditProductsActivity.this, "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    finish();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            InputProductImage.setImageURI(imageUri);
        }
    }

//    private void saveProductInfoToDatabase() {
//        HashMap<String, Object> productMap = new HashMap<>();
//        productMap.put("pid", productRandomKey);
//        productMap.put("Date", saveCurrentDate);
//        productMap.put("Time", saveCurrentTime);
//        productMap.put("Description", Description);
//        productMap.put("Image", downloadImageUrl);
//        productMap.put("Category", categoryName);
//        productMap.put("Price", Price);
//        productMap.put("Product_Name", ProductName);
//
//        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(AdminEditProductsActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    progress.dismiss();
//                    Toast.makeText(AdminEditProductsActivity.this, "Error, please try again", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });
//        specificProductRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    progress.dismiss();
//                    startActivity(new Intent(AdminEditProductsActivity.this, AdminCategoryActivity.class));
//                    Toast.makeText(AdminEditProductsActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    progress.dismiss();
//                    String message = task.getException().toString();
//                    Toast.makeText(AdminEditProductsActivity.this, "Error, please try again" + message, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        vendorProductsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(AdminEditProductsActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    progress.dismiss();
//                    Toast.makeText(AdminEditProductsActivity.this, "Error, please try again", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        });
//
//    }
}
