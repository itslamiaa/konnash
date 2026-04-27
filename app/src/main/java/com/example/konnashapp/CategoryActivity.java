package com.example.konnashapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {

    KonnashDatabase dbHelper;
    String selectedColor;
    Button confirmBtn;
    EditText nameEdit;
    ImageView closeBtn;
    View pickColorBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);
        dbHelper = new KonnashDatabase(this);

//        SQLiteDatabase myDb = dbHelper.getWritableDatabase();

        pickColorBtn = findViewById(R.id.pickColorBtn);
        closeBtn = findViewById(R.id.closeBtn);
        nameEdit = findViewById(R.id.editname);
        confirmBtn = findViewById(R.id.confirmbtn);
        selectedColor = "#4b729c";

        closeBtn.setOnClickListener(v -> {
            finish();
        });

        confirmBtn.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            if(name.isEmpty()){
                nameEdit.setError("أدخل اسم التصنيف");
                return;
            }
            long result = dbHelper.insertCategory(name,selectedColor);
            if(result!=-1){
                Toast.makeText(this, "تم إضافة التصنيف", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "خطأ في الإضافة", Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        pickColorBtn.setOnClickListener(v -> {
            ColorPicker sheet = new ColorPicker(color -> {
                selectedColor = String.format("#%06X", (0xFFFFFF & color));
                pickColorBtn.setBackgroundColor(color);
            });
            sheet.show(getSupportFragmentManager(), "color picker");
        });
    }
}
