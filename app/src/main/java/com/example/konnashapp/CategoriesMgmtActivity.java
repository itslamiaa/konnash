package com.example.konnashapp;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.content.Intent;
import android.database.Cursor;
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
    Button confirmBtn;
    Button addCategoryButton;

    ImageView noCategory;
    TextView noCategoryText;
    LinearLayout categoriesList;

    ArrayList<Integer> selectedCategoryIds = new ArrayList<>();
    ArrayList<String> selectedCategoryNames = new ArrayList<>();
    ArrayList<String> selectedCategoryColors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_mgmt);

        dbHelper = new KonnashDatabase(this);

        addCategoryButton = findViewById(R.id.add_category_button);
        closeBtn = findViewById(R.id.close);
        categoriesList = findViewById(R.id.categoriesContainer);
        confirmBtn = findViewById(R.id.confirmbtn);
        noCategory = findViewById(R.id.imagecategory);
        noCategoryText = findViewById(R.id.nocategorytext);
        if (getIntent() != null) {

            ArrayList<Integer> ids =
                    getIntent().getIntegerArrayListExtra("selected_ids");

            ArrayList<String> names =
                    getIntent().getStringArrayListExtra("selected_names");

            ArrayList<String> colors =
                    getIntent().getStringArrayListExtra("selected_colors");

            if (ids != null) selectedCategoryIds.addAll(ids);
            if (names != null) selectedCategoryNames.addAll(names);
            if (colors != null) selectedCategoryColors.addAll(colors);
        }

        addCategoryButton.setOnClickListener(v ->
                startActivity(new Intent(this, CategoryActivity.class))
        );

        closeBtn.setOnClickListener(v -> finish());

        confirmBtn.setOnClickListener(v -> {
            Intent resultIntent = new Intent();

            resultIntent.putIntegerArrayListExtra("selected_ids", selectedCategoryIds);
            resultIntent.putStringArrayListExtra("selected_names", selectedCategoryNames);
            resultIntent.putStringArrayListExtra("selected_colors", selectedCategoryColors);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCategories();
    }

    public void showCategories() {

        categoriesList.removeAllViews();
        cursor = dbHelper.getAllCategories();

        if (cursor == null || cursor.getCount() == 0) {
            noCategory.setVisibility(View.VISIBLE);
            noCategoryText.setVisibility(View.VISIBLE);
            categoriesList.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            return;
        }

        noCategory.setVisibility(View.GONE);
        noCategoryText.setVisibility(View.GONE);
        categoriesList.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);

        while (cursor.moveToNext()) {

            int categoryId = cursor.getInt(0);
            String name = cursor.getString(1);
            String color = cursor.getString(2);

            View item = getLayoutInflater()
                    .inflate(R.layout.name_category, categoriesList, false);

            TextView categoryName = item.findViewById(R.id.categoryName);
            CheckBox checkBox = item.findViewById(R.id.categoryCheck);
            ImageView menuBtn = item.findViewById(R.id.menuBtn);

            categoryName.setText(name);

            int mainColor = Color.parseColor(color);
            categoryName.setTextColor(mainColor);

            int lightColor = lightenColor(mainColor);
            if (categoryName.getBackground() != null) {
                categoryName.getBackground().setTint(lightColor);
            }

            menuBtn.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditCategoryActivity.class);
                intent.putExtra("category_id", categoryId);
                intent.putExtra("category_name", name);
                intent.putExtra("category_color", color);
                startActivity(intent);
            });

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(selectedCategoryIds.contains(categoryId));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    if (!selectedCategoryIds.contains(categoryId)) {
                        selectedCategoryIds.add(categoryId);
                        selectedCategoryNames.add(name);
                        selectedCategoryColors.add(color);
                    }
                } else {
                    selectedCategoryIds.remove((Integer) categoryId);
                    selectedCategoryNames.remove(name);
                    selectedCategoryColors.remove(color);
                }

            });

            categoriesList.addView(item);
        }

        cursor.close();
    }

    private int lightenColor(int color) {
        int r = (int) (red(color) + (255 - red(color)) * 0.7);
        int g = (int) (green(color) + (255 - green(color)) * 0.7);
        int b = (int) (blue(color) + (255 - blue(color)) * 0.7);

        return Color.rgb(r, g, b);
    }
}