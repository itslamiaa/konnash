package com.example.konnashapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class CustomerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

    }
}
