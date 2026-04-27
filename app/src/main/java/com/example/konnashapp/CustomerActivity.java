package com.example.konnashapp;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.hbb20.CountryCodePicker;

public class CustomerActivity extends AppCompatActivity {
    KonnashDatabase dbHelper;
    CountryCodePicker ccp;
    EditText customerName;
    EditText address;
    EditText phoneNumber;
    Button addCategory,confirmBtn;
    ImageView addressBtn;
    ImageView closeBtn;
    ActivityResultLauncher<Intent> addressLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        dbHelper = new KonnashDatabase(this);
        ccp = findViewById(R.id.ccp);
        phoneNumber = findViewById(R.id.phoneNumber);
        customerName = findViewById(R.id.name);

        ccp.registerCarrierNumberEditText(phoneNumber);


        addCategory = findViewById(R.id.add_category_button);
        addressBtn = findViewById(R.id.btnAddress);
        closeBtn = findViewById(R.id.closeBtn);
        address = findViewById(R.id.address);
        confirmBtn = findViewById(R.id.confirmbtn);

        addressLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String fullAddress = result.getData().getStringExtra("full_address");

                        if (fullAddress != null) {
                            address.setText(fullAddress);
                        }
                    }
                }
        );

        addressBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, AddressActivity.class);
            addressLauncher.launch(intent);
        });

        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerActivity.this, CategoriesMgmtActivity.class);
            startActivity(intent);
        });

        closeBtn.setOnClickListener(v -> {
            finish();
        });

        confirmBtn.setOnClickListener(v -> {
            String name = customerName.getText().toString().trim();
            String phone = phoneNumber.getText().toString().trim();
            String addr = address.getText().toString().trim();

            if (name.isEmpty()){
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
            if (customerId == -1){
                return;
            }else{

            }
            Intent intent = new Intent(CustomerActivity.this, CustomerProfileActivity.class);
            intent.putExtra("id",customerId);
            startActivity(intent);
        });

    }
}
