package com.example.konnashapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tabExpense, tabIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabExpense = findViewById(R.id.tab_expense);
        tabIncome = findViewById(R.id.tab_income);

        tabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabExpense.setBackgroundColor(getColor(android.R.color.holo_red_dark));
                tabExpense.setTextColor(getColor(android.R.color.white));
                tabIncome.setBackgroundColor(getColor(android.R.color.darker_gray));
                tabIncome.setTextColor(getColor(android.R.color.black));
            }
        });

        tabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabIncome.setBackgroundColor(getColor(android.R.color.holo_green_dark));
                tabIncome.setTextColor(getColor(android.R.color.white));
                tabExpense.setBackgroundColor(getColor(android.R.color.darker_gray));
                tabExpense.setTextColor(getColor(android.R.color.black));
            }
        });
    }
}