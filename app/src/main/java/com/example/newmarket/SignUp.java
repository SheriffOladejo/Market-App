package com.example.newmarket;

import android.content.Intent;
import android.graphics.Color;
import android.service.autofill.UserData;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class SignUp extends AppCompatActivity {

    private EditText firstname, address, lastname, email, password, confirm_password, phone_number;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Paper.init(this);

        progressBar = findViewById(R.id.sign_up_progress);
        Button createAccountButton = findViewById(R.id.signup);
        Button login = findViewById(R.id.create_login);
        firstname = findViewById(R.id.firstname);
        address = findViewById(R.id.create_address);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.create_email);
        password = findViewById(R.id.create_password);
        confirm_password = findViewById(R.id.create_confirm);
        phone_number = findViewById(R.id.create_phone);
        linearLayout = findViewById(R.id.sign_up_linear_layout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void useSnackBar(String snackBarMessage){
        Snackbar snackbar = Snackbar.make(linearLayout, snackBarMessage, Snackbar.LENGTH_SHORT);
        View snackView = snackbar.getView();
        TextView textView = snackView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void uploadToDatabase(String name, String surname, final String mail, final String pass, String phone, String address) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("Firstname", name);
        userData.put("Surname", surname);
        userData.put("Email", mail);
        userData.put("Password", pass);
        userData.put("Phone Number", phone);
        userData.put("Address", address);

        final DatabaseReference userRef = ref.child(phone);

        userRef.updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                useSnackBar("Account Created");
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(SignUp.this, LoginActivity.class));
                finish();
            }
            else{
                useSnackBar("Unable to create account");
                progressBar.setVisibility(View.INVISIBLE);
            }
            }
        });
    }

    private void createAccount(){
        String name = firstname.getText().toString();
        String surname = lastname.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String confirm_pass = confirm_password.getText().toString();
        String phone = phone_number.getText().toString();
        String homeAddress = address.getText().toString();

        if(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirm_pass.isEmpty() || phone.isEmpty()){
            useSnackBar("Fields should be filled");
        }
        else if(!pass.equals(confirm_pass)){
            useSnackBar("Passwords don't match");
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            //writeSignUpToDevice(name, surname, nick);
            uploadToDatabase(name, surname, mail, pass, phone, homeAddress);
        }
    }

}
