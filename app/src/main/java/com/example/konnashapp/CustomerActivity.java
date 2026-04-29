package com.example.konnashapp;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

public class CustomerActivity extends AppCompatActivity {

    KonnashDatabase dbHelper;

    CountryCodePicker ccp;
    EditText customerName, address, phoneNumber;

    Button addCategory, confirmBtn;
    ImageView addressBtn, closeBtn;

    TextView categoryHint;
    LinearLayout categoriesContainer;

    ActivityResultLauncher<Intent> addressLauncher;
    ActivityResultLauncher<Intent> categoryLauncher;

    ArrayList<Integer> selectedCategoryIds = new ArrayList<>();
    ArrayList<String> selectedCategoryNames = new ArrayList<>();
    ArrayList<String> selectedCategoryColors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        dbHelper = new KonnashDatabase(this);

        // views
        ccp = findViewById(R.id.ccp);
        phoneNumber = findViewById(R.id.phoneNumber);
        customerName = findViewById(R.id.name);
        address = findViewById(R.id.address);

        addCategory = findViewById(R.id.add_category_button);
        addressBtn = findViewById(R.id.btnAddress);
        closeBtn = findViewById(R.id.closeBtn);
        confirmBtn = findViewById(R.id.confirmbtn);

        categoryHint = findViewById(R.id.categoryHint);
        categoriesContainer = findViewById(R.id.selectedCategoriesContainer);

        ccp.registerCarrierNumberEditText(phoneNumber);

        // ---------------- ADDRESS ----------------
        addressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String fullAddress = result.getData().getStringExtra("full_address");
                        if (fullAddress != null) {
                            address.setText(fullAddress);
                        }
                    }
                }
        );

        addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            addressLauncher.launch(intent);
        });
        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoriesMgmtActivity.class);

            // pass currently selected categories so they stay checked
            intent.putIntegerArrayListExtra("selected_ids", selectedCategoryIds);
            intent.putStringArrayListExtra("selected_names", selectedCategoryNames);
            intent.putStringArrayListExtra("selected_colors", selectedCategoryColors);

            categoryLauncher.launch(intent);
        });
        // ---------------- CATEGORY SELECT ----------------
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

                        // clear old state (IMPORTANT FIX)
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



        // ---------------- CONFIRM ----------------
        confirmBtn.setOnClickListener(v -> {

            String name = customerName.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();
            String addr = address.getText().toString().trim();

            if (name.isEmpty()) {
                customerName.setError("حقل اجباري");
                customerName.requestFocus();
                return;
            }

            String fullNbr = ccp.getFullNumberWithPlus();

            long customerId = dbHelper.insertCustomer(
                    name,
                    fullNbr,
                    addr,
                    "",
                    ""
            );

            // assign categories safely
            for (Integer categoryId : selectedCategoryIds) {
                dbHelper.assignCategoryToCustomer((int) customerId, categoryId);
            }

            Intent intent = new Intent(this, PersonDetailActivity.class);
            intent.putExtra("id", customerId);
            startActivity(intent);
        });

        closeBtn.setOnClickListener(v -> finish());

        updateSelectedCategoriesUI();
    }

    // ---------------- CHIP UI ----------------
    private void updateSelectedCategoriesUI() {

        categoriesContainer.removeAllViews();

        if (selectedCategoryNames == null || selectedCategoryNames.isEmpty()) {
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

            if (chip.getBackground() != null) {
                chip.getBackground().setTint(lightColor);
            } else {
                chip.setBackgroundColor(lightColor);
            }

            categoriesContainer.addView(chip);
        }
    }

    // ---------------- COLOR EFFECT ----------------
    private int lightenColor(int color) {
        int r = (int) (red(color) + (255 - red(color)) * 0.7);
        int g = (int) (green(color) + (255 - green(color)) * 0.7);
        int b = (int) (blue(color) + (255 - blue(color)) * 0.7);

        return Color.rgb(r, g, b);
    }
}