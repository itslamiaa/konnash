package com.example.konnashapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        View pickColorBtn = findViewById(R.id.pickColorBtn);

        pickColorBtn.setOnClickListener(v -> {
            ColorPicker sheet = new ColorPicker(color -> {
                pickColorBtn.setBackgroundColor(color);
            });
            sheet.show(getSupportFragmentManager(), "color picker");
        });
    }
}
