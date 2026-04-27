package com.example.konnashapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    KonnashDatabase dbHelper;

    EditText name, phone, address;

    int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // make sure this matches your XML

        // DB
        dbHelper = new KonnashDatabase(this);

        // UI
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);

        // Get customer ID
        customerId = getIntent().getIntExtra("customer_id", -1);

        if (customerId != -1) {
            loadCustomerData(customerId);
        }
    }

    // 🔹 Load customer data
    private void loadCustomerData(int id) {
        Cursor cursor = dbHelper.getCustomerById(id);

        if (cursor != null && cursor.moveToFirst()) {

            String customerName = cursor.getString(1);
            String customerPhone = cursor.getString(2);
            String fullAddress = cursor.getString(6); // full_address column

            name.setText(customerName);
            phone.setText(customerPhone);
            address.setText(fullAddress);

            cursor.close();
        }
    }
}