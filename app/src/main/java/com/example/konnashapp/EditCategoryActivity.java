package com.example.konnashapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditCategoryActivity extends AppCompatActivity {
    TextView title;
    EditText editText;
    Button confirmBtn, deleteBtn;
    ImageView returnBtn;
    View pickColorBtn;

    KonnashDatabase dbHelper;

    int categoryId;
    String selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        dbHelper = new KonnashDatabase(this);
        editText = findViewById(R.id.categoryName);
        title = findViewById(R.id.title);
        returnBtn = findViewById(R.id.returnBtn);
        pickColorBtn = findViewById(R.id.pickColorBtn);
        confirmBtn = findViewById(R.id.confirmbtn);
        deleteBtn = findViewById(R.id.deletebtn);

        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        String categoryName = intent.getStringExtra("category_name");
        selectedColor = intent.getStringExtra("category_color");

        title.setText(categoryName);
        editText.setText(categoryName);
        pickColorBtn.setBackgroundColor(Color.parseColor(selectedColor));

        // return button
        returnBtn.setOnClickListener(v->finish());

        // color palette picker
        pickColorBtn.setOnClickListener(v -> {
            ColorPicker sheet = new ColorPicker(color -> {
                selectedColor = String.format("#%06X", (0xFFFFFF & color));
                pickColorBtn.setBackgroundColor(color);
            });
            sheet.show(getSupportFragmentManager(), "color picker");
        });

        // confirm button
        confirmBtn.setOnClickListener(v->{
            String newName = editText.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "الاسم فارغ", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.updateCategory(categoryId, newName, selectedColor);

            Toast.makeText(this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();

            finish();
        });

        // delete button
        deleteBtn.setOnClickListener(v -> {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.confirm_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Button cancel = dialog.findViewById(R.id.cancelbtn);
            Button delete = dialog.findViewById(R.id.deletebtn);

            cancel.setOnClickListener(v1 -> dialog.dismiss());

            delete.setOnClickListener(v12 -> {
                dbHelper.deleteCategory(categoryId);
                Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            });

            dialog.show();
        });

    }
}
