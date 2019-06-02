package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn;
    private Button saveButton;
    private Toolbar toolbar;
    private Uri mainImageUri = null;
    private RelativeLayout relativeLayout;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private String checker = "";
    private String image;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        relativeLayout = view.findViewById(R.id.settings_layout);
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = view.findViewById(R.id.settings_profile_image);
        fullNameEditText = view.findViewById(R.id.settings_fullname);
        userPhoneEditText = view.findViewById(R.id.settings_phone_number);
        addressEditText = view.findViewById(R.id.settings_address);
        profileChangeTextBtn = view.findViewById(R.id.profile_image_change_btn);
        saveButton = view.findViewById(R.id.settingUpdate);


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);
        imageUri = CropImage.getCaptureImageOutputUri(getActivity());

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
                        .start(getActivity());
            }
        });

        return view;
    }

    private void useSnackBar(String snackBarMessage) {
        Snackbar snackbar = Snackbar.make(relativeLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                mainImageUri = result.getUri();
                profileImageView.setImageURI(mainImageUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }

        }
        else{
            //Toast.makeText(SettingActivity.this, "Error, try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), SettingsFragment.class));
            useSnackBar("Problem encountered, please try again");
            getActivity().finish();
        }
    }

    private void userInfoSaved() {
        if(TextUtils.isEmpty(fullNameEditText.getText().toString()))
            useSnackBar("Name is required");
            //Toast.makeText(SettingActivity.this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
            useSnackBar("Address is required");
            //Toast.makeText(SettingActivity.this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
            useSnackBar("Phone number is required");
            //Toast.makeText(SettingActivity.this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        else if (checker.equals("clicked"))
            uploadImage();
    }

    private void uploadImage() {
        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Updating Profile");
        progress.setMessage("Please wait while we update your profile");
        progress.setCancelable(false);
        progress.show();

        if (imageUri != null) {
            String phone = LoginActivity.currentOnlineUser.getPhone();
            final StorageReference fileRef = storageProfilePictureRef
                    .child(phone + ".jpg");
            uploadTask = fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().toString();
                        myUrl = downloadUrl;

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
                        //Toast.makeText(SettingActivity.this, "Account information updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), HomeFragment.class));
                        useSnackBar("Profile updated");
                        getActivity().finish();
                    } else{
                        progress.dismiss();
                        useSnackBar("Error updating profile");
                        //Toast.makeText(SettingActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else{
            useSnackBar("No image is selected");
            //Toast.makeText(SettingActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
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

        useSnackBar("Profile updated");
        //Toast.makeText(SettingActivity.this, "Account information updated", Toast.LENGTH_SHORT).show();
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText) {

        String firstName = LoginActivity.currentOnlineUser.getFirstname();
        String lastName = LoginActivity.currentOnlineUser.getLastname();
        String phone = LoginActivity.currentOnlineUser.getPhone();
        image = LoginActivity.currentOnlineUser.getImage();
        fullNameEditText.setText(firstName + " " + lastName);
        userPhoneEditText.setText(phone);
        if(image.equals("")){

        }
        else{
            Picasso.get().load(image).into(profileImageView);
        }
    }

}
