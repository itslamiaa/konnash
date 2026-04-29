package com.example.konnashapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PersonDetailActivity extends AppCompatActivity {

    private TextView tvPersonName, tvContactInfo, tvBalance, tvTransactionCount;
    private LinearLayout layoutEmpty, transactionListContainer;
    private CardView btnTook, btnGave;

    private int    personId   = -1;
    private String personName  = "";
    private String personPhone = "";
    private double totalTook   = 0;
    private double totalGave   = 0;

    private KonnashDatabase db;
    private static final String CURRENCY = " د.ج";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        db = new KonnashDatabase(this);

        // Get data from Intent
        if (getIntent() != null) {
            personId    = getIntent().getIntExtra("person_id", -1);
            personName  = getIntent().getStringExtra("person_name") != null
                    ? getIntent().getStringExtra("person_name") : "";
            personPhone = getIntent().getStringExtra("person_phone") != null
                    ? getIntent().getStringExtra("person_phone") : "";
        }

        bindViews();
        setupActionButtons();
        setupBottomButtons();
        refreshUI();
    }

    private void bindViews() {
        tvPersonName           = findViewById(R.id.tvPersonName);
        tvContactInfo          = findViewById(R.id.tvContactInfo);
        tvBalance              = findViewById(R.id.tvBalance);
        tvTransactionCount     = findViewById(R.id.tvTransactionCount);
        layoutEmpty            = findViewById(R.id.layoutEmpty);
        transactionListContainer = findViewById(R.id.transactionListContainer);
        btnTook                = findViewById(R.id.btnTook);
        btnGave                = findViewById(R.id.btnGave);

        tvPersonName.setText(personName);
        tvContactInfo.setOnClickListener(v -> showContactDialog());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // ── 4 Action Buttons ─────────────────────────────────────────────
    private void setupActionButtons() {
        findViewById(R.id.btnReport).setOnClickListener(v ->
                Toast.makeText(this, "التقرير قريباً", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btnShare).setOnClickListener(v -> shareSummary());
        findViewById(R.id.btnCall).setOnClickListener(v -> callPerson());
        findViewById(R.id.btnNote).setOnClickListener(v -> showNoteDialog());
    }

    private void showContactDialog() {
        if (personPhone.isEmpty()) {
            Toast.makeText(this, "لا يوجد رقم هاتف", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(personName)
                .setMessage("الهاتف: " + personPhone)
                .setPositiveButton("اتصال", (d, w) -> callPerson())
                .setNegativeButton("إغلاق", null)
                .show();
    }

    private void callPerson() {
        if (personPhone.isEmpty()) {
            Toast.makeText(this, "لا يوجد رقم هاتف", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + personPhone)));
    }

    private void shareSummary() {
        double balance = totalTook - totalGave;
        String msg = "حساب " + personName + "\n"
                + "أخذت: "  + formatAmount(totalTook) + "\n"
                + "أعطيت: " + formatAmount(totalGave) + "\n"
                + "الرصيد: " + formatAmount(Math.abs(balance))
                + (balance >= 0 ? " (له عليك)" : " (عليه لك)");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(share, "مشاركة عبر"));
    }

    private void showNoteDialog() {
        EditText etNote = new EditText(this);
        etNote.setHint("اكتب ملاحظة...");
        etNote.setGravity(android.view.Gravity.END);
        etNote.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        new AlertDialog.Builder(this)
                .setTitle("ملاحظة")
                .setView(etNote)
                .setPositiveButton("حفظ", (d, w) -> {
                    String note = etNote.getText().toString().trim();
                    if (!note.isEmpty())
                        Toast.makeText(this, "تم حفظ الملاحظة", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ── Bottom Buttons: أخذت / أعطيت ─────────────────────────────────
    private void setupBottomButtons() {
        btnTook.setOnClickListener(v -> showTransactionDialog("took"));
        btnGave.setOnClickListener(v -> showTransactionDialog("gave"));
    }

    private void showTransactionDialog(String type) {
        String label = type.equals("took") ? "أخذت" : "أعطيت";

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        EditText etAmount = new EditText(this);
        etAmount.setHint("المبلغ (د.ج)");
        etAmount.setGravity(android.view.Gravity.END);
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(8);
        etAmount.setLayoutParams(lp);

        EditText etNote = new EditText(this);
        etNote.setHint("ملاحظة (اختياري)");
        etNote.setGravity(android.view.Gravity.END);

        layout.addView(etAmount);
        layout.addView(etNote);

        new AlertDialog.Builder(this)
                .setTitle(label)
                .setView(layout)
                .setPositiveButton("حفظ", (d, w) -> {
                    String amountStr = etAmount.getText().toString().trim();
                    if (amountStr.isEmpty()) {
                        Toast.makeText(this, "يرجى إدخال المبلغ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double amount = parseAmount(amountStr);
                    String note   = etNote.getText().toString().trim();
                    String date   = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(new Date());

                    // Save to DB using insertTransaction
                    db.insertTransaction(type, amount, personName, note, date);

                    if (type.equals("took")) totalTook += amount;
                    else                     totalGave += amount;

                    refreshUI();
                    Toast.makeText(this, "تم الحفظ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ── Refresh UI ────────────────────────────────────────────────────
    private void refreshUI() {
        // Recalculate from DB
        if (personId != -1) {
            totalTook = db.getTotalByCustomerAndType(personId, "took");
            totalGave = db.getTotalByCustomerAndType(personId, "gave");
        }

        double balance = totalTook - totalGave;
        tvBalance.setText(formatAmount(Math.abs(balance)));
        tvBalance.setTextColor(balance >= 0 ? 0xFF4CAF50 : 0xFFF44336);

        renderTransactions();
    }

    private void renderTransactions() {
        transactionListContainer.removeAllViews();

        android.database.Cursor cursor = db.getTransactionsByCustomer(personId);
        int count = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                count++;
                String type   = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String note   = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date   = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                transactionListContainer.addView(buildTransactionRow(type, amount, note, date));
            }
            cursor.close();
        }

        tvTransactionCount.setText("معاملات (" + count + ")");
        layoutEmpty.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    // ── Build transaction row ─────────────────────────────────────────
    private View buildTransactionRow(String type, double amount, String note, String date) {
        boolean isTook = type.equals("took");

        CardView card = new CardView(this);
        CardView.LayoutParams cp = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cp.bottomMargin = dpToPx(8);
        card.setLayoutParams(cp);
        card.setRadius(dpToPx(12));
        card.setCardElevation(0);
        card.setCardBackgroundColor(isTook ? 0xFFF0FFF4 : 0xFFFFF0F0);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvAmount = new TextView(this);
        tvAmount.setText(formatAmount(amount));
        tvAmount.setTextSize(16);
        tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);
        tvAmount.setTextColor(isTook ? 0xFF4CAF50 : 0xFFF44336);

        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(android.view.Gravity.END);

        TextView tvType = new TextView(this);
        tvType.setText(isTook ? "أخذت" : "أعطيت");
        tvType.setTextSize(14);
        tvType.setTypeface(null, android.graphics.Typeface.BOLD);
        tvType.setTextColor(0xFF333333);

        TextView tvNote = new TextView(this);
        tvNote.setText((note == null || note.isEmpty()) ? date : note + " · " + date);
        tvNote.setTextSize(11);
        tvNote.setTextColor(0xFF888888);

        right.addView(tvType);
        right.addView(tvNote);

        row.addView(tvAmount);
        row.addView(spacer);
        row.addView(right);
        card.addView(row);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private double parseAmount(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String formatAmount(double v) {
        return (v == (long) v ? String.valueOf((long) v) : String.valueOf(v)) + CURRENCY;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}