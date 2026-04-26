package com.example.konnashapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AddressActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        ImageView closeBtn = findViewById(R.id.close);
        Button confirm = findViewById(R.id.confirm_button);

        closeBtn.setOnClickListener(v -> {
            finish();
        });

        confirm.setOnClickListener(v -> {});
    }
}
