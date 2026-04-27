package com.example.konnashapp;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CategoriesMgmtActivity extends AppCompatActivity {

    KonnashDatabase dbHelper;
    Cursor cursor;
    ImageView closeBtn;
    Button addCategoryButton;
    ImageView noCategory;
    TextView noCategoryText;
    LinearLayout categoriesList;
    ArrayList<String> selectedCategories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_mgmt);

        // database
        dbHelper = new KonnashDatabase(this);

        // resources
        addCategoryButton = findViewById(R.id.add_category_button);
        closeBtn = findViewById(R.id.close);
        noCategory = findViewById(R.id.imagecategory);
        noCategoryText = findViewById(R.id.nocategorytext);
        categoriesList = findViewById(R.id.categoriesContainer);

        // moves to category activity
        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesMgmtActivity.this, CategoryActivity.class);
            startActivity(intent);
        });


        // closes categories management
        closeBtn.setOnClickListener(v -> finish());


    }

    @Override
    protected void onResume() {
        super.onResume();
        showCategories();
    }

    // shows categories in db in categories mgmt interface
    public void showCategories() {
        categoriesList.removeAllViews();
        cursor = dbHelper.getAllCategories();

        if (cursor == null || cursor.getCount() == 0) {
            noCategory.setVisibility(View.VISIBLE);
            noCategoryText.setVisibility(View.VISIBLE);
            categoriesList.setVisibility(View.GONE);
            if (cursor != null) {
                cursor.close();
            }
        } else {
            noCategory.setVisibility(View.GONE);
            noCategoryText.setVisibility(View.GONE);
            categoriesList.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {

                int categoryId = cursor.getInt(0);
                String name = cursor.getString(1);
                String color = cursor.getString(2);

                int customerCount = dbHelper.getClientCountByCategory(categoryId);
                int supplierCount = 0;

                View item = getLayoutInflater().inflate(R.layout.name_category, categoriesList, false);

                TextView categoryName = item.findViewById(R.id.categoryName);
                TextView categoryDetails = item.findViewById(R.id.categoryDetails);
                ImageView menuBtn = item.findViewById(R.id.menuBtn);
                categoryName.setText(name);

                menuBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(CategoriesMgmtActivity.this, EditCategoryActivity.class);
                    intent.putExtra("category_id", categoryId);
                    intent.putExtra("category_name", name);
                    intent.putExtra("category_color", color);
                    startActivity(intent);
                });

                int mainColor = Color.parseColor(color);
                categoryName.setTextColor(mainColor);
                categoryDetails.setText(customerCount + " عميل • " + supplierCount + " مورد");

                int lightColor = lightenColor(mainColor);
                if (categoryName.getBackground() != null) {
                    categoryName.getBackground().setTint(lightColor);
                }

                CheckBox checkBox = item.findViewById(R.id.categoryCheck);


                // checkbox logic
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(selectedCategories.contains(name));

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedCategories.add(name);
                    } else {
                        selectedCategories.remove(name);
                    }
                });
                categoriesList.addView(item);


            }
            cursor.close();
        }
    }

    private int lightenColor(int color) {
        int red = (int) (red(color) + (255 - red(color)) * 0.7);
        int green = (int) (green(color) + (255 - green(color)) * 0.7);
        int blue = (int) (blue(color) + (255 - blue(color)) * 0.7);

        return Color.rgb(red, green, blue);

    }


}
