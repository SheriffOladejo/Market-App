package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProductFragment extends Fragment {

    private String productID, imageURL, name, product_discount, price, desc, Description,discount, Price, ProductName, categoryName;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDesc, InputProductPrice, Discount;
    private static final int GalleryPick = 1;
    private Uri imageUri;
    private Button AddNewProductButton;
    private StorageReference productImagesRef;
    private StorageReference filePath;
    private DatabaseReference productRef, productCategoryRef, vendorProductRef;
    private ProgressDialog progress;
    private HashMap<String, Object> productMap;

    public EditProductFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);
        AddNewProductButton = view.findViewById(R.id.apply_changes);
        Discount = view.findViewById(R.id.product_discount_edit);
        InputProductName = view.findViewById(R.id.product_name_edit);
        InputProductDesc = view.findViewById(R.id.product_description_edit);
        InputProductPrice = view.findViewById(R.id.product_price_edit);
        InputProductImage = view.findViewById(R.id.product_image_edit);

        progress = new ProgressDialog(getActivity());

        Bundle bundle = this.getArguments();
        if(bundle != null){
            productID = bundle.getString("pid");
            imageURL = bundle.getString("Image");
            name = bundle.getString("Product Name");
            product_discount = bundle.getString("Discount");
            price = bundle.getString("Price");
            desc = bundle.getString("Description");
        }
        Discount.setText(product_discount);
        InputProductPrice.setText(price);
        InputProductName.setText(name);
        InputProductDesc.setText(desc);
        Picasso.get().load(imageURL).into(InputProductImage);

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

        //productImagesRef = FirebaseStorage.getInstance().getReference().child("Product images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        vendorProductRef = FirebaseDatabase.getInstance().getReference().child("Vendors").child(LoginActivity.currentOnlineVendor.getPhone()).child("Products");

//        InputProductImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });
        productCategoryRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        return view;
    }

    private void validateProductData() {
        Description = InputProductDesc.getText().toString();
        Price = InputProductPrice.getText().toString();
        ProductName = InputProductName.getText().toString();
        if(Discount.getText().toString()==""){
            discount = "0";
        }
        else{
            discount = Discount.getText().toString();
        }

        if(TextUtils.isEmpty(Description))
            Toast.makeText(getActivity(), "Product description is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(ProductName))
            Toast.makeText(getActivity(), "Product name is mandatory", Toast.LENGTH_SHORT).show();
        else if(TextUtils.isEmpty(Price))
            Toast.makeText(getActivity(), "Product price is mandatory", Toast.LENGTH_SHORT).show();
        else{
            //StoreProductInformation();
            progress.setTitle("Updating...");
            progress.setMessage("Please Wait");
            progress.show();
            updateProductInfoToProductRef();
        }
    }

    private void updateProductInfoToProductRef(){
        productMap = new HashMap<>();
        productMap.put("Price", Price);
        productMap.put("Description", Description);
        //productMap.put("Image", downloadImageUrl);
        productMap.put("Product_Name", ProductName);
        productMap.put("Discount", discount);

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
        productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateProductToVendorRef();
                }
                else{
                    progress.dismiss();
                    Toast.makeText(getActivity(), "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
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
                    Toast.makeText(getActivity(), "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
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
                    Toast.makeText(getActivity(), "Product updated successfully", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    startActivity(new Intent(getActivity(), AdminActivity.class));
                }
                else{
                    Toast.makeText(getActivity(), "Unable to update details, please try again", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    getActivity().finish();
                }
            }
        });
    }

}
