package com.example.newmarket;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveButton;
    private Uri mainImageUri = null;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText = findViewById(R.id.settings_fullname);
        userPhoneEditText = findViewById(R.id.settings_phone_number);
        addressEditText = findViewById(R.id.settings_address);
        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        saveButton = findViewById(R.id.update_account_settings_btn);

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageUri = CropImage.getCaptureImageOutputUri(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    userInfoSaved();
                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SettingActivity.this);
            }
        });

        Paper.init(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(requestCode == RESULT_OK){
                mainImageUri = result.getUri();
                profileImageView.setImageURI(mainImageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
        else{
            Toast.makeText(SettingActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this, SettingActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
            Toast.makeText(SettingActivity.this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
            Toast.makeText(SettingActivity.this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
            Toast.makeText(SettingActivity.this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        else if (checker.equals("clicked"))
            uploadImage();
    }

    private void uploadImage() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Updating Profile");
        progress.setMessage("Please wait while we update your profile");
        progress.setCancelable(false);
        progress.show();

        if(imageUri != null){
            String phone = LoginActivity.currentOnlineUser.getPhone();
            final StorageReference fileRef = storageProfilePictureRef
                                             .child(phone + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("Firstname", fullNameEditText.getText().toString());
                        userMap.put("Address", addressEditText.getText().toString());
                        userMap.put("Phone", userPhoneEditText.getText().toString());
                        userMap.put("Profile Image", myUrl);
                        ref.child(LoginActivity.currentOnlineUser.getPhone()).updateChildren(userMap);
                        LoginActivity.currentOnlineUser.setImage(myUrl);
                        LoginActivity.currentOnlineUser.setPhone(userPhoneEditText.getText().toString());

                        progress.dismiss();
                        Toast.makeText(SettingActivity.this, "Account information updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        finish();
                    }
                    else
                        progress.dismiss();
                        Toast.makeText(SettingActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(SettingActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("Firstname", fullNameEditText.getText().toString());
        userMap.put("Address", addressEditText.getText().toString());
        userMap.put("Phone", userPhoneEditText.getText().toString());
        ref.child(LoginActivity.currentOnlineUser.getPhone()).updateChildren(userMap);

        LoginActivity.currentOnlineUser.setPhone(addressEditText.getText().toString());
        LoginActivity.currentOnlineUser.setFirstname(fullNameEditText.getText().toString());
        LoginActivity.currentOnlineUser.setAddress(addressEditText.getText().toString());

        Toast.makeText(SettingActivity.this, "Account information updated", Toast.LENGTH_SHORT).show();
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {

        String firstName = LoginActivity.currentOnlineUser.getFirstname();
        String lastName = LoginActivity.currentOnlineUser.getLastname();
        String phone = LoginActivity.currentOnlineUser.getPhone();
        image = LoginActivity.currentOnlineUser.getImage();
        fullNameEditText.setText(firstName + " " + lastName);
        userPhoneEditText.setText(phone);
        if(image.equals("")){
            Toast.makeText(SettingActivity.this, "No profile image", Toast.LENGTH_SHORT).show();
        }
        else{
            Picasso.get().load(image).into(profileImageView);
        }

//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(LoginActivity.currentOnlineUser.getPhone());
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    if(dataSnapshot.child("Profile Image").exists()){
//                        String image = dataSnapshot.getValue().toString();
////                        String name = dataSnapshot.child("Firstname").getValue().toString();
////                        String phone = dataSnapshot.child("Phone").getValue().toString();
////                        String address = dataSnapshot.child("Address").getValue().toString();
//
//                        if(image.equals("")){
//
//                        }
//                        else{
//                        Picasso.get().load(image).into(profileImageView);
//                        }
////                        fullNameEditText.setText(name);
////                        userPhoneEditText.setText(phone);
////                        addressEditText.setText(address);
//                    }
//                }
//            }
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
}