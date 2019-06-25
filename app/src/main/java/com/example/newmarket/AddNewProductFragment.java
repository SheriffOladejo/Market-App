package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

public class AddNewProductFragment extends Fragment {

    private String categoryName, vendor, downloadImageUrl, Description, Discount, productID, Price, ProductName, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    public static ImageView InputProductImage;
    private EditText InputProductName, InputProductDesc, InputProductPrice, Disount_price;
    private RadioGroup radioGroup;
    static public Uri imageUri = null;
    private StorageReference productImagesRef;
    private StorageReference filePath;
    private DatabaseReference productRef, specificProductRef, vendorProductsRef;
    private ProgressDialog progress;
    private HashMap<String, Object> productMap;
    private Bitmap compressedImageFile;
    private byte[] datum;

    public AddNewProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_product, container, false);
        AddNewProductButton = view.findViewById(R.id.add_button);
        radioGroup = view.findViewById(R.id.group);
        InputProductName = view.findViewById(R.id.product_name);
        InputProductDesc = view.findViewById(R.id.product_description);
        InputProductPrice = view.findViewById(R.id.product_price);
        Disount_price = view.findViewById(R.id.discount_price);
        InputProductImage = view.findViewById(R.id.select_product_image);
        progress = new ProgressDialog(getActivity());
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
        return view;
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
        else{
            Toast.makeText(getActivity(), "Please select a category", Toast.LENGTH_SHORT).show();
        }
        specificProductRef = FirebaseDatabase.getInstance().getReference().child(categoryName);

        if(TextUtils.isEmpty(Disount_price.getText().toString())){
            Discount = "0";
        }
        else{
            Discount = Disount_price.getText().toString();
        }
        if(imageUri == null){
            Toast.makeText(getActivity(), "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
            Toast.makeText(getActivity(), "Product description is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(ProductName))
            Toast.makeText(getActivity(), "Product name is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(Price))
            Toast.makeText(getActivity(), "Product price is mandatory", Toast.LENGTH_SHORT).show();
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

        File newImageFile = new File(imageUri.getPath());
        try{
            compressedImageFile = new Compressor(getActivity())
                .setMaxHeight(200)
                .setMaxWidth(200)
                .setQuality(6)
                .compressToBitmap(newImageFile);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        datum = baos.toByteArray();
        filePath = productImagesRef.child(categoryName).child(productID);
        final UploadTask uploadTask = filePath.putBytes(datum);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            String message = e.getMessage();
            Toast.makeText(getActivity(), "Error: " + message, Toast.LENGTH_SHORT).show();
            progress.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Got product image successfully", Toast.LENGTH_SHORT).show();
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
        productMap.put("Vendor_Phone", LoginActivity.currentOnlineVendor.getPhone());
        productMap.put("Vendor", LoginActivity.currentOnlineVendor.getFirstname());

        productRef.child(productID).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadToVendor();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
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
                    Toast.makeText(getActivity(), "Error, please try again", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
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
                    Toast.makeText(getActivity(), "Product was added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), AdminCategoryActivity.class));
                }
                else{
                    progress.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(getActivity(), "Error, please try again" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .start(getActivity());

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK){
//                imageUri = result.getUri();
//                InputProductImage.setImageURI(imageUri);
//            }
//            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                Exception error = result.getError();
//            }
//
//        }
//    }

}
