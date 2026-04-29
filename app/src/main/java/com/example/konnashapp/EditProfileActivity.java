package com.example.konnashapp;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

    KonnashDatabase dbHelper;

    EditText name, phone, address;
    Button confirmBtn, deleteBtn, addCategoryBtn;
    ImageView returnBtn;

    LinearLayout categoriesContainer;
    TextView categoryHint;

    int customerId;

    // 🔥 SAME SYSTEM AS CustomerActivity
    ArrayList<Integer> selectedCategoryIds = new ArrayList<>();
    ArrayList<String> selectedCategoryNames = new ArrayList<>();
    ArrayList<String> selectedCategoryColors = new ArrayList<>();

    ActivityResultLauncher<Intent> categoryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new KonnashDatabase(this);

        // UI
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phoneNumber);
        address = findViewById(R.id.address);
        confirmBtn = findViewById(R.id.confirmbtn);
        deleteBtn = findViewById(R.id.deletebtn);
        returnBtn = findViewById(R.id.returnBtn);

        addCategoryBtn = findViewById(R.id.add_category_button);
        categoriesContainer = findViewById(R.id.selectedCategoriesContainer);
        categoryHint = findViewById(R.id.categoryHint);

        customerId = getIntent().getIntExtra("customer_id", -1);

        if (customerId == -1) {
            finish();
            return;
        }

        loadCustomerData(customerId);
        loadCustomerCategories(customerId);

        returnBtn.setOnClickListener(v -> finish());

        deleteBtn.setOnClickListener(v -> {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.confirm_dialog);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            Button cancel = dialog.findViewById(R.id.cancelbtn);
            Button delete = dialog.findViewById(R.id.deletebtn);

            cancel.setOnClickListener(v1 -> dialog.dismiss());

            delete.setOnClickListener(v12 -> {
                dbHelper.deleteCustomer(customerId);
                Toast.makeText(this, "تم حذف العميل بنجاح", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            });

            dialog.show();
        });

        categoryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        ArrayList<Integer> ids =
                                result.getData().getIntegerArrayListExtra("selected_ids");

                        ArrayList<String> names =
                                result.getData().getStringArrayListExtra("selected_names");

                        ArrayList<String> colors =
                                result.getData().getStringArrayListExtra("selected_colors");

                        selectedCategoryIds.clear();
                        selectedCategoryNames.clear();
                        selectedCategoryColors.clear();

                        if (ids != null) selectedCategoryIds.addAll(ids);
                        if (names != null) selectedCategoryNames.addAll(names);
                        if (colors != null) selectedCategoryColors.addAll(colors);

                        updateSelectedCategoriesUI();
                    }
                }
        );

        addCategoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriesMgmtActivity.class);

            intent.putIntegerArrayListExtra("selected_ids", selectedCategoryIds);
            intent.putStringArrayListExtra("selected_names", selectedCategoryNames);
            intent.putStringArrayListExtra("selected_colors", selectedCategoryColors);

            categoryLauncher.launch(intent);
        });

        confirmBtn.setOnClickListener(v -> {

            String newName = name.getText().toString().trim();
            String newPhone = phone.getText().toString().trim();
            String newAddress = address.getText().toString().trim();

            if (newName.isEmpty()) {
                name.setError("حقل اجباري");
                return;
            }

            dbHelper.insertCustomer(newName, newPhone, newAddress, "", "");

            dbHelper.getWritableDatabase().delete(
                    "CustomerCategory",
                    "customer_id=?",
                    new String[]{String.valueOf(customerId)}
            );

            for (Integer catId : selectedCategoryIds) {
                dbHelper.assignCategoryToCustomer(customerId, catId);
            }

            Toast.makeText(this, "تم التحديث", Toast.LENGTH_SHORT).show();
        });
    }


    private void loadCustomerData(int id) {

        Cursor cursor = dbHelper.getCustomerById(id);

        if (cursor != null && cursor.moveToFirst()) {

            name.setText(cursor.getString(1));
            phone.setText(cursor.getString(2));
            address.setText(cursor.getString(6));

            cursor.close();
        }
    }


    private void loadCustomerCategories(int id) {

        Cursor cursor = dbHelper.getCategoriesByCustomerId(id);

        selectedCategoryIds.clear();
        selectedCategoryNames.clear();
        selectedCategoryColors.clear();

        while (cursor.moveToNext()) {

            int catId = cursor.getInt(0);
            String catName = cursor.getString(1);
            String catColor = cursor.getString(2);

            selectedCategoryIds.add(catId);
            selectedCategoryNames.add(catName);
            selectedCategoryColors.add(catColor);
        }

        cursor.close();

        updateSelectedCategoriesUI();
    }

    private void updateSelectedCategoriesUI() {

        categoriesContainer.removeAllViews();

        if (selectedCategoryNames.isEmpty()) {
            categoryHint.setVisibility(View.VISIBLE);
            categoriesContainer.setVisibility(View.GONE);
            return;
        }

        categoryHint.setVisibility(View.GONE);
        categoriesContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < selectedCategoryNames.size(); i++) {

            String name = selectedCategoryNames.get(i);
            String colorStr = selectedCategoryColors.get(i);

            TextView chip = new TextView(this);
            chip.setText(name);

            chip.setPadding(30, 12, 30, 12);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(10, 5, 10, 5);
            chip.setLayoutParams(params);

            chip.setTextSize(20);
            chip.setBackgroundResource(R.drawable.category_pill);

            int mainColor = Color.parseColor(colorStr);
            int lightColor = lightenColor(mainColor);

            chip.setTextColor(mainColor);
            chip.getBackground().setTint(lightColor);

            categoriesContainer.addView(chip);
        }
    }

    private int lightenColor(int color) {
        int r = (int) (Color.red(color) + (255 - Color.red(color)) * 0.7);
        int g = (int) (Color.green(color) + (255 - Color.green(color)) * 0.7);
        int b = (int) (Color.blue(color) + (255 - Color.blue(color)) * 0.7);

        return Color.rgb(r, g, b);
    }
}