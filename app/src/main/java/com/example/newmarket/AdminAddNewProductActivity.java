package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, vendor, downloadImageUrl, Description, Discount, productID, Price, ProductName, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDesc, InputProductPrice, Disount_price;
    private RadioGroup radioGroup;
    private Uri imageUri = null;
    private StorageReference productImagesRef;
    private StorageReference filePath;
    private DatabaseReference productRef, specificProductRef, vendorProductsRef;
    private ProgressDialog progress;
    private HashMap<String, Object> productMap;
    private Bitmap compressedImageFile;
    private byte[] datum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        AddNewProductButton = findViewById(R.id.add_button);
        radioGroup = findViewById(R.id.group);
        InputProductName = findViewById(R.id.product_name);
        InputProductDesc = findViewById(R.id.product_description);
        InputProductPrice = findViewById(R.id.product_price);
        Disount_price = findViewById(R.id.discount_price);
        InputProductImage = findViewById(R.id.select_product_image);
        progress = new ProgressDialog(this);
        vendor = LoginActivity.currentOnlineVendor.getPhone();

        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        vendorProductsRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(LoginActivity.currentOnlineVendor.getPhone()).child("Products");

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
    }

    private void validateProductData() {
        Description = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString().toLowerCase();
        int categoryID = radioGroup.getCheckedRadioButtonId();
        if(categoryID == R.id.bags)
            categoryName = "Bags";
        else if(categoryID == R.id.beauty)
            categoryName = "Beauty Products";
        else if(categoryID == R.id.books_and_stationery)
            categoryName = "Books and Stationery";
        else if(categoryID == R.id.clothing)
            categoryName = "Clothing";
        else if(categoryID == R.id.edibles)
            categoryName = "Edibles";
        else if(categoryID == R.id.electronics)
            categoryName = "Electronics";
        else if(categoryID == R.id.laptops)
            categoryName = "Laptops";
        else if(categoryID == R.id.laptop_accessories)
            categoryName = "Laptop Accessories";
        else if(categoryID == R.id.phone_accessory)
            categoryName = "Phone Accessories";
        else if(categoryID == R.id.pharmaceuticals)
            categoryName = "Pharmaceuticals";
        else if(categoryID == R.id.shoes)
            categoryName = "Shoes";
        else if(categoryID == R.id.wristwatch_and_other)
            categoryName = "Wrist Watches and other accessories";
        else if(categoryID == R.id.others)
            categoryName = "Others";
        specificProductRef = FirebaseDatabase.getInstance().getReference().child(categoryName);

        if(TextUtils.isEmpty(Disount_price.getText().toString())){
            Discount = "0";
        }
        else{
            Discount = Disount_price.getText().toString();
        }
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
        progress.setTitle("Uploading Product.");
        progress.setMessage("Dear Admin, please wait while we upload your product.");
        progress.setCancelable(false);
        progress.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productID = saveCurrentDate + saveCurrentTime;

//        File newImageFile = new File(imageUri.getPath());
//        try {
//            compressedImageFile = new Compressor(AdminAddNewProductActivity.this)
//                    .setMaxWidth(200)
//                    .setMaxHeight(200)
//                    .setQuality(6)
//                    .compressToBitmap(newImageFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] thumbnail = baos.toByteArray();
        File newImageFile = new File(imageUri.getPath());
        try{
            compressedImageFile = new Compressor(AdminAddNewProductActivity.this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(6)
                    .compressToBitmap(newImageFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        datum = baos.toByteArray();
        filePath = productImagesRef.child(categoryName).child(imageUri.getLastPathSegment() + productID + ".jpg");
        final UploadTask uploadTask = filePath.putBytes(datum);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            String message = e.getMessage();
            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            progress.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminAddNewProductActivity.this, "Got product image successfully", Toast.LENGTH_SHORT).show();
                saveProductInfoToDatabase();
                }
                }
            });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        productMap = new HashMap<>();
        productMap.put("pid", productID);
        productMap.put("Date", saveCurrentDate);
        productMap.put("Time", saveCurrentTime);
        productMap.put("Description", Description);
        productMap.put("Image", downloadImageUrl);
        productMap.put("Category", categoryName);
        productMap.put("Price", Price);
        productMap.put("Product_Name", ProductName);
        productMap.put("Discount", Discount);
        productMap.put("Vendor", vendor);

        productRef.child(productID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                uploadToVendor();
            }
            else{
                progress.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Error, please try again", Toast.LENGTH_SHORT).show();
                finish();
            }
            }
        });

    }

    private void uploadToVendor(){
        vendorProductsRef.child(productID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                uploadToCategory();
            }
            else{
                progress.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Error, please try again", Toast.LENGTH_SHORT).show();
                finish();
            }
            }
        });
    }

    private void uploadToCategory(){
        specificProductRef.child(productID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                progress.dismiss();
                Toast.makeText(AdminAddNewProductActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));
            }
            else{
                progress.dismiss();
                String message = task.getException().toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error, please try again" + message, Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    private void openGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AdminAddNewProductActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode== RESULT_OK){
                imageUri = result.getUri();
                InputProductImage.setImageURI(imageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
    }

}

//        if(categoryName.equals("Mobile Phones")){
//            filePath = productImagesRef.child(categoryName).child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Sport Items")){
//            filePath = productImagesRef.child("Sport Items").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Beauty")){
//            filePath = productImagesRef.child("Beauty").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Drugs and Health")){
//            filePath = productImagesRef.child("Drugs and Health").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Fashion Accessories")){
//            filePath = productImagesRef.child("Fashion Accessories").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Electronics")){
//            filePath = productImagesRef.child("Electronics").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Shoes")){
//            filePath = productImagesRef.child("Shoes").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Books and Stationery")){
//            filePath = productImagesRef.child("Books and Stationery").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Mobiles")){
//            filePath = productImagesRef.child("Mobiles").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Edibles")){
//            filePath = productImagesRef.child("Edibles").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }
//        else if(categoryName.equals("Others")){
//            filePath = productImagesRef.child("Others").child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
//        }

