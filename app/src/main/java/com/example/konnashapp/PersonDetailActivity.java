package com.example.konnashapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class PersonDetailActivity extends AppCompatActivity {

    // ─── UI ──────────────────────────────────────────────────────────
    private TextView tvPersonName, tvContactInfo, tvBalance, tvTransactionCount;
    private LinearLayout layoutEmpty, transactionListContainer;
    private CardView btnTook, btnGave;

    // ─── Data passed from previous screen ────────────────────────────
    private String personName  = "";
    private String personPhone = "";
    private double totalTook   = 0.0;
    private double totalGave   = 0.0;

    // ─── Simple transaction model ─────────────────────────────────────
    static class Transaction {
        static final int TOOK = 0;
        static final int GAVE = 1;

        String note;
        double amount;
        int    type; // TOOK or GAVE
        String date;

        Transaction(String note, double amount, int type, String date) {
            this.note   = note;
            this.amount = amount;
            this.type   = type;
            this.date   = date;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();
    private static final String CURRENCY = " د.ج";

    // ═════════════════════════════════════════════════════════════════
    //  onCreate
    // ═════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        // Receive data from intent (sent from ActivityCustomer row click)
        if (getIntent() != null) {
            personName  = getIntent().getStringExtra("person_name")  != null
                    ? getIntent().getStringExtra("person_name") : "—";
            personPhone = getIntent().getStringExtra("person_phone") != null
                    ? getIntent().getStringExtra("person_phone") : "";
            totalTook   = getIntent().getDoubleExtra("person_took", 0.0);
            totalGave   = getIntent().getDoubleExtra("person_gave", 0.0);
        }

        bindViews();
        setupHeader();
        setupActionButtons();
        setupBottomButtons();
        refreshUI();
    }

    // ─── Bind views ───────────────────────────────────────────────────
    private void bindViews() {
        tvPersonName         = findViewById(R.id.tvPersonName);
        tvContactInfo        = findViewById(R.id.tvContactInfo);
        tvBalance            = findViewById(R.id.tvBalance);
        tvTransactionCount   = findViewById(R.id.tvTransactionCount);
        layoutEmpty          = findViewById(R.id.layoutEmpty);
        transactionListContainer = findViewById(R.id.transactionListContainer);
        btnTook              = findViewById(R.id.btnTook);
        btnGave              = findViewById(R.id.btnGave);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // ═════════════════════════════════════════════════════════════════
    //  HEADER
    // ═════════════════════════════════════════════════════════════════
    private void setupHeader() {
        tvPersonName.setText(personName);

        // Tap subtitle → show contact info dialog
        tvContactInfo.setOnClickListener(v -> showContactDialog());
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

    // ═════════════════════════════════════════════════════════════════
    //  4 ACTION BUTTONS
    // ═════════════════════════════════════════════════════════════════
    private void setupActionButtons() {

        // تقرير
        findViewById(R.id.btnReport).setOnClickListener(v ->
                Toast.makeText(this, "التقرير قريباً", Toast.LENGTH_SHORT).show());

        // مشاركة
        findViewById(R.id.btnShare).setOnClickListener(v -> shareSummary());

        // اتصال
        findViewById(R.id.btnCall).setOnClickListener(v -> callPerson());

        // ملاحظة
        findViewById(R.id.btnNote).setOnClickListener(v -> showNoteDialog());
    }

    private void callPerson() {
        if (personPhone.isEmpty()) {
            Toast.makeText(this, "لا يوجد رقم هاتف", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + personPhone));
        startActivity(intent);
    }

    private void shareSummary() {
        double balance = totalTook - totalGave;
        String msg = "حساب " + personName + "\n"
                + "أخذت: " + formatAmount(totalTook) + "\n"
                + "أعطيت: " + formatAmount(totalGave) + "\n"
                + "الرصيد: " + formatAmount(Math.abs(balance))
                + (balance >= 0 ? " (له عليك)" : " (عليه لك)");

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(share, "مشاركة عبر"));
    }

    private void showNoteDialog() {
        android.widget.EditText etNote = new android.widget.EditText(this);
        etNote.setHint("اكتب ملاحظة...");
        etNote.setGravity(android.view.Gravity.END);
        int pad = dpToPx(16);
        etNote.setPadding(pad, pad, pad, pad);

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

    // ═════════════════════════════════════════════════════════════════
    //  BOTTOM BUTTONS: أخذت / أعطيت
    // ═════════════════════════════════════════════════════════════════
    private void setupBottomButtons() {
        btnTook.setOnClickListener(v -> showTransactionDialog(Transaction.TOOK));
        btnGave.setOnClickListener(v -> showTransactionDialog(Transaction.GAVE));
    }

    private void showTransactionDialog(int type) {
        String label = (type == Transaction.TOOK) ? "أخذت" : "أعطيت";

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(16);
        layout.setPadding(pad, pad, pad, pad);

        android.widget.EditText etAmount = new android.widget.EditText(this);
        etAmount.setHint("المبلغ (د.ج)");
        etAmount.setGravity(android.view.Gravity.END);
        etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(8);
        etAmount.setLayoutParams(lp);

        android.widget.EditText etNote = new android.widget.EditText(this);
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
                    String date   = getCurrentDate();

                    // ── TODO: your friend saves this to DB here ──
                    transactions.add(new Transaction(note, amount, type, date));

                    if (type == Transaction.TOOK) totalTook += amount;
                    else                          totalGave += amount;

                    refreshUI();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ═════════════════════════════════════════════════════════════════
    //  REFRESH UI
    // ═════════════════════════════════════════════════════════════════
    private void refreshUI() {
        // Balance
        double balance = totalTook - totalGave;
        tvBalance.setText(formatAmount(Math.abs(balance)));
        tvBalance.setTextColor(balance >= 0 ? 0xFF4CAF50 : 0xFFF44336);

        // Transaction count
        tvTransactionCount.setText("معاملات (" + transactions.size() + ")");

        // Empty state
        layoutEmpty.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);

        // Render list
        renderTransactions();
    }

    private void renderTransactions() {
        transactionListContainer.removeAllViews();
        for (int i = transactions.size() - 1; i >= 0; i--) {
            transactionListContainer.addView(buildTransactionRow(transactions.get(i)));
        }
    }

    // ─── Build one transaction row ────────────────────────────────────
    private View buildTransactionRow(Transaction t) {
        CardView card = new CardView(this);
        CardView.LayoutParams cp = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT);
        cp.bottomMargin = dpToPx(8);
        card.setLayoutParams(cp);
        card.setRadius(dpToPx(12));
        card.setCardElevation(0);
        card.setCardBackgroundColor(
                t.type == Transaction.TOOK ? 0xFFF0FFF4 : 0xFFFFF0F0);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // LEFT: amount
        TextView tvAmount = new TextView(this);
        tvAmount.setText(formatAmount(t.amount));
        tvAmount.setTextSize(16);
        tvAmount.setTypeface(null, android.graphics.Typeface.BOLD);
        tvAmount.setTextColor(t.type == Transaction.TOOK ? 0xFF4CAF50 : 0xFFF44336);

        // SPACER
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));

        // RIGHT: note + date
        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(android.view.Gravity.END);

        TextView tvType = new TextView(this);
        tvType.setText(t.type == Transaction.TOOK ? "أخذت" : "أعطيت");
        tvType.setTextSize(14);
        tvType.setTypeface(null, android.graphics.Typeface.BOLD);
        tvType.setTextColor(0xFF333333);

        TextView tvNote = new TextView(this);
        tvNote.setText(t.note.isEmpty() ? t.date : t.note + " · " + t.date);
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

    // ═════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════
    private double parseAmount(String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String formatAmount(double amount) {
        if (amount == (long) amount) return (long) amount + CURRENCY;
        return amount + CURRENCY;
    }

    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}