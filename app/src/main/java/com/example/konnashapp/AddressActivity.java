package com.example.konnashapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddressActivity extends AppCompatActivity {
    EditText addressEdit;
    EditText cityEdit;
    EditText countryEdit;
    Button confirmBtn;
    ImageView closeBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        closeBtn = findViewById(R.id.close);
        confirmBtn = findViewById(R.id.confirm_button);
        addressEdit = findViewById(R.id.address);
        cityEdit = findViewById(R.id.city);
        countryEdit = findViewById(R.id.country);

        closeBtn.setOnClickListener(v -> {
            finish();
        });

        confirmBtn.setOnClickListener(v -> {
            String address = addressEdit.getText().toString().trim();
            String city = cityEdit.getText().toString().trim();
            String country = countryEdit.getText().toString().trim();

            StringBuilder fullAddress = new StringBuilder();
            if (!address.isEmpty()) fullAddress.append(address);
            if (!city.isEmpty()) {
                if (fullAddress.length() > 0) fullAddress.append(", ");
                fullAddress.append(city);
            }
            if (!country.isEmpty()) {
                if (fullAddress.length() > 0) fullAddress.append(", ");
                fullAddress.append(country);
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("full_address",fullAddress.toString());

            setResult(RESULT_OK,resultIntent);
            finish();
        });
    }
}
