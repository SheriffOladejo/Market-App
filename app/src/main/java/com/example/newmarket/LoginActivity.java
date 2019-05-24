package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.newmarket.Model.Users;
import com.example.newmarket.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText Phone, Password;
    private Button Login ,SignUp;
    private ProgressDialog progress;
    private String parentDbName = "Users";
    private String rememberTag;
    private TextView admin, notAdmin;
    private CheckBox Remember;
    public static Users currentOnlineUser;
    public static Users currentOnlineVendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);

        Phone = findViewById(R.id.login_phone);
        Password = findViewById(R.id.login_password);
        Login = findViewById(R.id.login_button);
        SignUp = findViewById(R.id.create_account);
        progress = new ProgressDialog(this);
        admin = findViewById(R.id.admin);
        notAdmin = findViewById(R.id.not_admin);
        Remember = findViewById(R.id.remember);
        currentOnlineUser = new Users();
        currentOnlineVendor = new Users();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(parentDbName == "Users"){
            loginUser();
            }
            else
                loginAdmin();

            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.setText("Login as Vendor");
                Remember.setVisibility(View.INVISIBLE);
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUp.class));
            }
        });

        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Remember.setVisibility(View.VISIBLE);
            Login.setText("Login");
            admin.setVisibility(View.VISIBLE);
            notAdmin.setVisibility(View.INVISIBLE);
            parentDbName = "Users";
            }
        });

    }

    private void loginAdmin() {
        final String phone = Phone.getText().toString().toLowerCase();
        final String password = Password.getText().toString().toLowerCase();
        if(phone.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Fields must be filled", Toast.LENGTH_SHORT).show();
        }
        else{
            initDialog(progress);
        final DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference().child("Admins").child(phone);
        loginRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userPassword = dataSnapshot.child("Password").getValue().toString();
                    if(userPassword.equals(password)){
                        currentOnlineVendor.setEmail(dataSnapshot.child("Email").getValue().toString());
                        currentOnlineVendor.setFirstname(dataSnapshot.child("Vendor").getValue().toString());
                        currentOnlineVendor.setPhone(dataSnapshot.child("Phone Number").getValue().toString());
                        progress.dismiss();
                        Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, AdminCategoryActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }

    }

    private void loginUser(){
        final String phone = Phone.getText().toString().toLowerCase();
        final String password = Password.getText().toString().toLowerCase();
        if(phone.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Fields must be filled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            initDialog(progress);
            final DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
            loginRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String userPassword = dataSnapshot.child("Password").getValue().toString();
                        currentOnlineUser.setEmail(dataSnapshot.child("Email").getValue().toString());
                        currentOnlineUser.setFirstname(dataSnapshot.child("Firstname").getValue().toString());
                        currentOnlineUser.setNickname(dataSnapshot.child("Nickname").getValue().toString());
                        currentOnlineUser.setPassword(dataSnapshot.child("Password").getValue().toString());
                        currentOnlineUser.setPhone(dataSnapshot.child("Phone Number").getValue().toString());
                        currentOnlineUser.setLastname(dataSnapshot.child("Surname").getValue().toString());
                        currentOnlineUser.setImage(dataSnapshot.child("Profile Image").getValue().toString());
                        if(userPassword.equals(password)){
                            if(Remember.isChecked()){
                            Paper.book().write(Prevalent.currentOnlineUser.getPhone(), phone);
                            Paper.book().write(Prevalent.currentOnlineUser.getPassword(), password);
                            }
                            progress.dismiss();
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void initDialog(ProgressDialog dialog){
        dialog.setTitle("Logging in");
        dialog.setMessage("Logging you in...");
        dialog.setCancelable(false);
        dialog.show();
    }

//    private void AllowAccess(final String UserPhoneKey, final String UserPasswordKey) {
//        final DatabaseReference rootRef;
//        rootRef = FirebaseDatabase.getInstance().getReference();
//
//        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(parentDbName).child(UserPhoneKey).exists()){
//                    Users usersData = dataSnapshot.child("Users").child(UserPhoneKey).getValue(Users.class);
//                    if(usersData.getPhone().equals(UserPhoneKey)){
//                        if(usersData.getPassword().equals(UserPasswordKey)){
//                            //Toast.makeText(LauncherActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            Prevalent.currentOnlineUser = usersData;
//                            startActivity(intent);
//                        }
//                    }
//                }
//                else{
//                    Toast.makeText(LoginActivity.this, "Account doesn't exist, please create a new account", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(LoginActivity.this, SignUp.class);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void allowAccessToAccount(final String phone, final String password) {
//
//        if(Remember.isChecked()){
//            Paper.book().write(Prevalent.UserPhoneKey, phone);
//            Paper.book().write(Prevalent.UserPasswordKey, password);
//        }
//
//        final DatabaseReference rootRef;
//        rootRef = FirebaseDatabase.getInstance().getReference();
//
//        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.child(parentDbName).child(phone).exists()){
//                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
//                    if(usersData.getPhone().equals(phone)){
//                        if(usersData.getPassword().equals(password)){
//                            if (parentDbName.equals("Admins")){
//                                //Toast.makeText(LoginActivity.this, "Logged in successfully as an admin", Toast.LENGTH_SHORT).show();
//                                progress.dismiss();
//                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
//                                startActivity(intent);
//                            }
//                            else if(parentDbName.equals("Users")){
//                                //Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
//                                progress.dismiss();
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                Prevalent.currentOnlineUser = usersData;
//                                startActivity(intent);
//                            }
//                        }
//                        else{
//                            progress.dismiss();
//                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                else{
//                    Toast.makeText(LoginActivity.this, "Account does not exist", Toast.LENGTH_SHORT).show();
//                    progress.dismiss();
//                    Toast.makeText(LoginActivity.this, "Please create a new account", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
