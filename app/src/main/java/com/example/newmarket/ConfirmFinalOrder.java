package com.example.newmarket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.newmarket.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ConfirmFinalOrder extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;
    private Button confirmOrderButton;

    private String totalAmount = "";
    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        Paper.init(this);
        String userphone = LoginActivity.currentOnlineUser.getPhone();
        phone = userphone;

        totalAmount = getIntent().getStringExtra("Total Price");

        confirmOrderButton = findViewById(R.id.confirm_final_order_btn);
        nameEditText = findViewById(R.id.shipment_name);
        addressEditText = findViewById(R.id.shipment_address);
        phoneEditText = findViewById(R.id.shipment_phone_number);

        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())
                || TextUtils.isEmpty(addressEditText.getText().toString())
                || TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(ConfirmFinalOrder.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }
        else{
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentDate;
        final String saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
        .child("Vendors")
        .child(phone);

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("Total_Amount", totalAmount);
        ordersMap.put("Name", nameEditText.getText().toString());
        ordersMap.put("Phone", phoneEditText.getText().toString());
        ordersMap.put("Date", saveCurrentDate);
        ordersMap.put("Time", saveCurrentTime);
        ordersMap.put("Address", addressEditText.getText().toString());
        ordersMap.put("State", "Not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                FirebaseDatabase.getInstance().getReference().child("Cart List")
                    .child("User View")
                    .child(phone)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ConfirmFinalOrder.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ConfirmFinalOrder.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        }
                    });
            }
            else{
                Toast.makeText(ConfirmFinalOrder.this, "Unable to confirm order, please try again", Toast.LENGTH_SHORT).show();
            }
            }
        });

    }
}
