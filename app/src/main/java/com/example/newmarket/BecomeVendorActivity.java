package com.example.newmarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newmarket.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class BecomeVendorActivity extends AppCompatActivity {

    private Button createAccountButton, Login;
    private EditText email, password, confirm_password, phone_number, company_name;
    private ProgressDialog progress;
    private String phone, mail, pass, confirm_pass, vendor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_vendor);

        Paper.init(this);

        createAccountButton = findViewById(R.id.signup);
        Login = findViewById(R.id.create_login);
        email = findViewById(R.id.create_email);
        password = findViewById(R.id.create_password);
        confirm_password = findViewById(R.id.create_confirm);
        phone_number = findViewById(R.id.create_phone);
        progress = new ProgressDialog(this);
        company_name= findViewById(R.id.company_name);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void uploadToDatabase(final String mail, final String pass, String phone, String company_name) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Vendors");
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("Email", mail);
        userData.put("Password", pass);
        userData.put("Phone Number", phone);
        userData.put("Vendor", company_name);

        final DatabaseReference userRef = ref.child(phone);

        userRef.updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                Toast.makeText(BecomeVendorActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                startActivity(new Intent(BecomeVendorActivity.this, LoginActivity.class));
                finish();
            }
            else{
                Toast.makeText(BecomeVendorActivity.this, "Problem encountered while creating account", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
            }
        });
    }

    private void initDialogBox(ProgressDialog dialog){
        dialog.setTitle("Creating Account");
        dialog.setMessage("Relax while we create your account");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void createAccount(){
        mail = email.getText().toString();
        pass = password.getText().toString();
        confirm_pass = confirm_password.getText().toString();
        phone = phone_number.getText().toString();
        vendor = company_name.getText().toString();

        if(mail.isEmpty() || pass.isEmpty() || confirm_pass.isEmpty() || phone.isEmpty()){
            Toast.makeText(BecomeVendorActivity.this, "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirm_pass)){
            Toast.makeText(BecomeVendorActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }
        else{
            initDialogBox(progress);
            uploadToDatabase(mail, pass, phone, vendor);
        }
    }
}

