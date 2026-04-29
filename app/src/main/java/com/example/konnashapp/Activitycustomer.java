package com.example.konnashapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Activitycustomer extends AppCompatActivity {

    private LinearLayout tabClients, tabSuppliers, clientListContainer;
    private TextView tvTook, tvGave, tvCustomerCount;
    private EditText etSearch;
    private ExtendedFloatingActionButton fabAddClient;

    private boolean isClientsTabActive = true;
    private KonnashDatabase db;
    private static final String CURRENCY = " د.ج";

    // ── Simple model to hold customer + balance ───────────────────────
    static class CustomerRow {
        int    id;
        String name, phone;
        double took, gave;

        CustomerRow(int id, String name, String phone, double took, double gave) {
            this.id = id; this.name = name; this.phone = phone;
            this.took = took; this.gave = gave;
        }
    }

    // ── onCreate ──────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        db = new KonnashDatabase(this);

        bindViews();
        setupTabs();
        setupSearch();
        setupFab();
        setupBottomNav();
        displayCurrentTab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCurrentTab(); // refresh when returning from PersonDetailActivity
    }

    private void bindViews() {
        tabClients          = findViewById(R.id.tabClients);
        tabSuppliers        = findViewById(R.id.tabSuppliers);
        clientListContainer = findViewById(R.id.clientListContainer);
        tvTook              = findViewById(R.id.tvTook);
        tvGave              = findViewById(R.id.tvGave);
        tvCustomerCount     = findViewById(R.id.tvCustomerCount);
        etSearch            = findViewById(R.id.etSearch);
        fabAddClient        = findViewById(R.id.fabAddClient);
    }

    // ── Tabs ──────────────────────────────────────────────────────────
    private void setupTabs() {
        tabClients.setOnClickListener(v   -> switchTab(true));
        tabSuppliers.setOnClickListener(v -> switchTab(false));
    }

    private void switchTab(boolean clientsActive) {
        isClientsTabActive = clientsActive;

        tabClients.setBackgroundColor(clientsActive ? 0xFFE8F0F8 : 0xFFFFFFFF);
        ((TextView) tabClients.getChildAt(1)).setTextColor(clientsActive ? 0xFF4a90d9 : 0xFF888888);
        ((TextView) tabClients.getChildAt(1)).setTypeface(null,
                clientsActive ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        tabSuppliers.setBackgroundColor(!clientsActive ? 0xFFE8F0F8 : 0xFFFFFFFF);
        ((TextView) tabSuppliers.getChildAt(1)).setTextColor(!clientsActive ? 0xFF4a90d9 : 0xFF888888);
        ((TextView) tabSuppliers.getChildAt(1)).setTypeface(null,
                !clientsActive ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

        fabAddClient.setText(clientsActive ? "إضافة عميل" : "إضافة مورد");
        etSearch.setText("");
        displayCurrentTab();
    }

    // ── Search ────────────────────────────────────────────────────────
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndDisplay(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ── FAB ───────────────────────────────────────────────────────────
    private void setupFab() {
        fabAddClient.setOnClickListener(v -> showAddDialog());
    }

    private void showAddDialog() {
        String label = isClientsTabActive ? "عميل" : "مورد";
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        EditText etName  = makeInput("الاسم *", false);
        EditText etPhone = makeInput("رقم الهاتف", false);

        layout.addView(etName);
        layout.addView(etPhone);

        new AlertDialog.Builder(this)
                .setTitle("إضافة " + label)
                .setView(layout)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String name  = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "يرجى إدخال الاسم", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.insertCustomer(name, phone, "", "", "");
                    displayCurrentTab();
                    Toast.makeText(this, "تمت الإضافة", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ── Edit / Delete ─────────────────────────────────────────────────
    private void showEditDeleteDialog(CustomerRow customer) {
        new AlertDialog.Builder(this)
                .setTitle(customer.name)
                .setItems(new String[]{"تعديل", "حذف"}, (dialog, which) -> {
                    if (which == 0) showEditDialog(customer);
                    else            confirmDelete(customer);
                }).show();
    }

    private void showEditDialog(CustomerRow customer) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        EditText etName  = makeInput("الاسم *", false);
        EditText etPhone = makeInput("رقم الهاتف", false);

        etName.setText(customer.name);
        etPhone.setText(customer.phone);

        layout.addView(etName);
        layout.addView(etPhone);

        new AlertDialog.Builder(this)
                .setTitle("تعديل")
                .setView(layout)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String name  = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "يرجى إدخال الاسم", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // update using existing insertCustomer logic via delete+reinsert
                    // or ask your friend to add an updateCustomer() method in KonnashDatabase
                    db.deleteCustomer(customer.id);
                    db.insertCustomer(name, phone, "", "", "");
                    displayCurrentTab();
                    Toast.makeText(this, "تم التعديل", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void confirmDelete(CustomerRow customer) {
        new AlertDialog.Builder(this)
                .setTitle("حذف")
                .setMessage("هل تريد حذف " + customer.name + "؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    db.deleteCustomer(customer.id);
                    displayCurrentTab();
                    Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    // ── Load from DB ──────────────────────────────────────────────────
    private List<CustomerRow> loadCustomersFromDB() {
        List<CustomerRow> list = new ArrayList<>();
        Cursor cursor = db.getAllCustomers(); // we'll use getAllCustomers below
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int    id    = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name  = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

                // Calculate balance from transactions for this customer
                double took = db.getTotalByCustomerAndType(id, "took");
                double gave = db.getTotalByCustomerAndType(id, "gave");

                list.add(new CustomerRow(id, name, phone, took, gave));
            }
            cursor.close();
        }
        return list;
    }

    // ── Display ───────────────────────────────────────────────────────
    private void displayCurrentTab() {
        filterAndDisplay(etSearch.getText().toString().trim());
    }

    private void filterAndDisplay(String query) {
        List<CustomerRow> all      = loadCustomersFromDB();
        List<CustomerRow> filtered = new ArrayList<>();

        for (CustomerRow c : all) {
            if (!query.isEmpty()
                    && !c.name.contains(query)
                    && !c.phone.contains(query)) continue;
            filtered.add(c);
        }
        renderList(filtered);
    }

    private void renderList(List<CustomerRow> list) {
        clientListContainer.removeAllViews();
        double totalTook = 0, totalGave = 0;

        for (CustomerRow c : list) {
            totalTook += c.took;
            totalGave += c.gave;
            clientListContainer.addView(buildRow(c));
        }

        tvTook.setText(formatAmount(totalTook));
        tvGave.setText(formatAmount(totalGave));
        tvCustomerCount.setText(isClientsTabActive
                ? "العملاء (" + list.size() + ")"
                : "الموردين (" + list.size() + ")");
    }

    // ── Build one row ─────────────────────────────────────────────────
    private View buildRow(CustomerRow customer) {
        CardView card = new CardView(this);
        CardView.LayoutParams cp = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        cp.bottomMargin = dpToPx(8);
        card.setLayoutParams(cp);
        card.setRadius(dpToPx(12));
        card.setCardElevation(0);
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        double balance = customer.took - customer.gave;

        // LEFT: balance
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);

        TextView tvBalance = new TextView(this);
        tvBalance.setText(formatAmount(Math.abs(balance)));
        tvBalance.setTextSize(16);
        tvBalance.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBalance.setTextColor(balance >= 0 ? 0xFF4CAF50 : 0xFFF44336);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(balance >= 0 ? "له عليك" : "عليه لك");
        tvLabel.setTextSize(11);
        tvLabel.setTextColor(0xFF888888);

        left.addView(tvBalance);
        left.addView(tvLabel);

        // SPACER
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));

        // RIGHT: name + phone
        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(android.view.Gravity.END);

        TextView tvName = new TextView(this);
        tvName.setText(customer.name);
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(0xFF333333);

        TextView tvPhone = new TextView(this);
        tvPhone.setText(customer.phone.isEmpty() ? "—" : customer.phone);
        tvPhone.setTextSize(12);
        tvPhone.setTextColor(0xFF888888);

        right.addView(tvName);
        right.addView(tvPhone);

        row.addView(left);
        row.addView(spacer);
        row.addView(right);
        card.addView(row);

        // Click → open detail screen
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonDetailActivity.class);
            intent.putExtra("person_id",    customer.id);
            intent.putExtra("person_name",  customer.name);
            intent.putExtra("person_phone", customer.phone);
            intent.putExtra("person_took",  customer.took);
            intent.putExtra("person_gave",  customer.gave);
            startActivity(intent);
        });

        // Long press → edit / delete
        card.setOnLongClickListener(v -> {
            showEditDeleteDialog(customer);
            return true;
        });

        return card;
    }

    // ── Bottom Nav ────────────────────────────────────────────────────
    private void setupBottomNav() {
        findViewById(R.id.navDebtBook).setOnClickListener(v ->
                Toast.makeText(this, "دفتر الديون", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navCashBook).setOnClickListener(v ->
                Toast.makeText(this, "دفتر النقدية", Toast.LENGTH_SHORT).show());
        findViewById(R.id.navMore).setOnClickListener(v ->
                Toast.makeText(this, "المزيد", Toast.LENGTH_SHORT).show());
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private EditText makeInput(String hint, boolean numeric) {
        EditText et = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(8);
        et.setLayoutParams(lp);
        et.setHint(hint);
        et.setGravity(android.view.Gravity.END);
        if (numeric) et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return et;
    }

    private String formatAmount(double v) {
        return (v == (long) v ? String.valueOf((long) v) : String.valueOf(v)) + CURRENCY;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}