package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText Phone, Password;
    private Button Login, SignUp;
    private ProgressDialog progress;
    private String parentDbName = "Users";
    private TextView admin, notAdmin;
    private CheckBox Remember;
    public static Users currentOnlineUser;
    public static Users currentOnlineVendor;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Paper.init(this);
        linearLayout = findViewById(R.id.login_linear_layout);
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
                if (parentDbName == "Users") {
                    loginUser();
                } else
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

        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(userPasswordKey != "" && userPhoneKey != ""){
            if(!TextUtils.isEmpty(userPasswordKey) && !TextUtils.isEmpty(userPhoneKey)){
                allowAccess(userPhoneKey);
            }
        }
    }

    private void allowAccess(String userPhoneKey) {
        initDialog(progress);
        DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userPhoneKey);
        loginRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentOnlineUser.setEmail(dataSnapshot.child("Email").getValue().toString());
                    currentOnlineUser.setFirstname(dataSnapshot.child("Firstname").getValue().toString());
                    currentOnlineUser.setNickname(dataSnapshot.child("Nickname").getValue().toString());
                    currentOnlineUser.setPassword(dataSnapshot.child("Password").getValue().toString());
                    currentOnlineUser.setPhone(dataSnapshot.child("Phone Number").getValue().toString());
                    currentOnlineUser.setLastname(dataSnapshot.child("Surname").getValue().toString());
                    currentOnlineUser.setImage(dataSnapshot.child("Profile Image").getValue().toString());
                    progress.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                else{
                    useSnackBar("Account not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void useSnackBar(String snackBarMessage) {
        Snackbar snackbar = Snackbar.make(linearLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void loginAdmin() {
        final String phone = Phone.getText().toString().toLowerCase();
        final String password = Password.getText().toString().toLowerCase();
        if (phone.isEmpty() || password.isEmpty()) {
            useSnackBar("Fields should be filled");
        } else {
            initDialog(progress);
            final DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference().child("Admins").child(phone);
            loginRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userPassword = dataSnapshot.child("Password").getValue().toString();
                        if (userPassword.equals(password)) {
                            currentOnlineVendor.setEmail(dataSnapshot.child("Email").getValue().toString());
                            currentOnlineVendor.setFirstname(dataSnapshot.child("Vendor").getValue().toString());
                            currentOnlineVendor.setPhone(dataSnapshot.child("Phone Number").getValue().toString());
                            progress.dismiss();
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            finish();
                        } else {
                            useSnackBar("Incorrect Password");
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



    private void loginUser() {
        final String phone = Phone.getText().toString().toLowerCase();
        final String password = Password.getText().toString().toLowerCase();
        if (phone.isEmpty() || password.isEmpty()) {
            useSnackBar("Fields should be filled");
        } else {
            initDialog(progress);
            final DatabaseReference loginRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
            loginRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userPassword = dataSnapshot.child("Password").getValue().toString();
                        currentOnlineUser.setEmail(dataSnapshot.child("Email").getValue().toString());
                        currentOnlineUser.setFirstname(dataSnapshot.child("Firstname").getValue().toString());
                        currentOnlineUser.setNickname(dataSnapshot.child("Nickname").getValue().toString());
                        currentOnlineUser.setPassword(dataSnapshot.child("Password").getValue().toString());
                        currentOnlineUser.setPhone(dataSnapshot.child("Phone Number").getValue().toString());
                        currentOnlineUser.setLastname(dataSnapshot.child("Surname").getValue().toString());
                        currentOnlineUser.setImage(dataSnapshot.child("Profile Image").getValue().toString());
                        if (userPassword.equals(password)) {
                            if (Remember.isChecked()) {
                                Paper.book().write(Prevalent.UserPhoneKey, phone);
                                Paper.book().write(Prevalent.UserPasswordKey, password);
                                progress.dismiss();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                            else {
                            progress.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            }
                        } else {
                            useSnackBar("Incorrect Password");
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

    private void initDialog(ProgressDialog dialog) {
        dialog.setTitle("Logging in");
        dialog.setMessage("Logging you in...");
        dialog.setCancelable(false);
        dialog.show();
    }
}