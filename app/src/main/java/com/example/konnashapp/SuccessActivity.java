package com.example.konnashapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // استقبال المبلغ
        String amount = getIntent().getStringExtra("amount");

        TextView tvAmount = findViewById(R.id.tvAmount);
        TextView tvDate = findViewById(R.id.tvDate);

        tvAmount.setText(amount + " د.ج.");

        // التاريخ والوقت
        String date = new SimpleDateFormat("Aujourd'hui à HH:mm", Locale.getDefault()).format(new Date());
        tvDate.setText(date);

        // زر انهاء
        findViewById(R.id.btnFinish).setOnClickListener(v -> finish());

        // زر مشاركة
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            // يمكن تضيف share functionality هنا
        });
    }
}
