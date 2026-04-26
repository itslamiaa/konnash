package com.example.konnashapp;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.hbb20.CountryCodePicker;

public class CustomerActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        CountryCodePicker countryCodePicker = findViewById(R.id.ccp);
        EditText phoneNumber = findViewById(R.id.phoneNumber);

        countryCodePicker.registerCarrierNumberEditText(phoneNumber);

        Button addCategory = findViewById(R.id.add_category_button);
        ImageView addressBtn = findViewById(R.id.btnAddress);

        addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this , AddressActivity.class);
            startActivity(intent);
        });

        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, CategoryActivity.class);
            startActivity(intent);
        });


    }
}
