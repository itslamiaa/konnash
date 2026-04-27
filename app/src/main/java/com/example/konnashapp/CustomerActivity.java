package com.example.konnashapp;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.hbb20.CountryCodePicker;

public class CustomerActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText customerName;
    EditText address;
    EditText phoneNumber;
    Button addCategory;
    ImageView addressBtn;
    ImageView closeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        ccp = findViewById(R.id.ccp);
        phoneNumber = findViewById(R.id.phoneNumber);

        ccp.registerCarrierNumberEditText(phoneNumber);

        if (ccp.isValidFullNumber()){
            String fullNbr = ccp.getFullNumberWithPlus();
        }
        else {
            phoneNumber.setError("رقم هاتف غير صحيح");
        }

        addCategory = findViewById(R.id.add_category_button);
        addressBtn = findViewById(R.id.btnAddress);
        closeBtn = findViewById(R.id.closeBtn);

        addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, AddressActivity.class);
            startActivity(intent);
        });

        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, CategoriesMgmtActivity.class);
            startActivity(intent);
        });

        closeBtn.setOnClickListener(v -> {
            finish();
        });


    }
}
