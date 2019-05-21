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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import io.paperdb.Paper;

public class SignUp extends AppCompatActivity {

    private EditText firstname, lastname, nickname, email, password, confirm_password, phone_number;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Paper.init(this);

        Button createAccountButton = findViewById(R.id.signup);
        Button login = findViewById(R.id.create_login);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        nickname = findViewById(R.id.nickname);
        email = findViewById(R.id.create_email);
        password = findViewById(R.id.create_password);
        confirm_password = findViewById(R.id.create_confirm);
        phone_number = findViewById(R.id.create_phone);
        progress = new ProgressDialog(this);

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


    /*private void writeSignUpToDevice(String name, String surname, String nick){
        Paper.book().write(Prevalent.UserFirstnameKey, name);
        Paper.book().write(Prevalent.UserSurnameKey, surname);
        Paper.book().write(Prevalent.UserNicknamekey, nick);
        Paper.book().write(Prevalent.UserEmailKey, email);
    }*/

    private void uploadToDatabase(String name, String surname, String nick, final String mail, final String pass, String phone) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("Firstname", name);
        userData.put("Surname", surname);
        userData.put("Nickname", nick);
        userData.put("Email", mail);
        userData.put("Password", pass);
        userData.put("Phone Number", phone);
        userData.put("Profile Image", "");

        final DatabaseReference userRef = ref.child(phone);

        userRef.updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUp.this, "Account created", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    startActivity(new Intent(SignUp.this, LoginActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(SignUp.this, "Problem encountered while creating account", Toast.LENGTH_SHORT).show();
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
        String name = firstname.getText().toString();
        String surname = lastname.getText().toString();
        String nick = nickname.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String confirm_pass = confirm_password.getText().toString();
        String phone = phone_number.getText().toString();

        if(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirm_pass.isEmpty() || phone.isEmpty()){
            Toast.makeText(SignUp.this, "All fields should be filled", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirm_pass)){
            Toast.makeText(SignUp.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }
        else{
            initDialogBox(progress);
            //writeSignUpToDevice(name, surname, nick);
            uploadToDatabase(name, surname, nick, mail, pass, phone);
        }
        }
    }


/*
    private void validateEmail(final String name, final String phone, final String pass, final String surname, final String nickname, final String email) {
    final DatabaseReference rootRef;
    rootRef = FirebaseDatabase.getInstance().getReference();

    HashMap<String, Object> userDataMap = new HashMap<>();
    userDataMap.put("Firstname", name);
    userDataMap.put("Lastname", surname);
    userDataMap.put("Password", pass);
    userDataMap.put("Email", email);
    userDataMap.put("Phone", phone);
    Paper.book().write(Prevalent.UserEmailKey, email);
    Paper.book().write(Prevalent.UserFirstname, name);
    userDataMap.put("Nickname", nickname);
    if(nickname != ""){
    Paper.book().write(Prevalent.UserNickname, nickname);
    }
    rootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
    if(task.isSuccessful()){
    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
    if(task.isSuccessful()){
    Toast.makeText(SignUp.this, "Account created successfully", Toast.LENGTH_SHORT).show();
    progress.dismiss();
    }
    else{
    Toast.makeText(SignUp.this, "Unable to create account", Toast.LENGTH_SHORT).show();
    progress.dismiss();
    }
    }
    });
    progress.dismiss();

    Intent intent = new Intent(SignUp.this, MainActivity.class);
    startActivity(intent);
    }
    else{
    Toast.makeText(SignUp.this, "Unable to create account", Toast.LENGTH_SHORT).show();
    progress.dismiss();
    }
    }
    });
    final DatabaseReference rootRef;
    rootRef = FirebaseDatabase.getInstance().getReference();
    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    if (!(dataSnapshot.child("Users").child(phone).exists())){
    HashMap<String, Object> userDataMap = new HashMap<>();
    userDataMap.put("Firstname", name);
    userDataMap.put("Lastname", surname);
    userDataMap.put("Password", pass);
    userDataMap.put("Email", email);
    userDataMap.put("Phone", phone);
    userDataMap.put("Nickname", nickname);

    rootRef.child("Users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
    if(task.isSuccessful()){
    Toast.makeText(SignUp.this, "Account created successfully", Toast.LENGTH_SHORT).show();
    progress.dismiss();

    Intent intent = new Intent(SignUp.this, MainActivity.class);
    startActivity(intent);
    }
    else{
    Toast.makeText(SignUp.this, "Unable to create account", Toast.LENGTH_SHORT).show();
    progress.dismiss();
    }
    }
    });
    }
    else{
    Toast.makeText(SignUp.this, "Email and or Phone number already in use by another user.", Toast.LENGTH_SHORT).show();
    progress.dismiss();
    Toast.makeText(SignUp.this, "Please try again", Toast.LENGTH_SHORT).show();
    }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
    });
*/



