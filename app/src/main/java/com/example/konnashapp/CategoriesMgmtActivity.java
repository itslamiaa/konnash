package com.example.konnashapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class CategoriesMgmtActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_mgmt);

        Button addCategoryButton = findViewById(R.id.add_category_button);
        ImageView closeBtn = findViewById(R.id.close);

        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesMgmtActivity.this, CategoryActivity.class);
            startActivity(intent);
        });

        closeBtn.setOnClickListener(v -> {
            finish();
        });


    }
}
