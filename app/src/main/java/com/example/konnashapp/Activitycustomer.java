package com.example.konnashapp;

import android.app.AlertDialog;
import android.content.Intent;
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
    private final List<Person> allPersons = new ArrayList<>();
    private static final String CURRENCY = " د.ج";
    private int nextId = 1;

    // ── Person model ──────────────────────────────────────────────────
    static class Person {
        static final int TYPE_CLIENT   = 0;
        static final int TYPE_SUPPLIER = 1;
        int id;
        String name, phone;
        double took, gave;
        int type;

        Person(int id, String name, String phone, double took, double gave, int type) {
            this.id = id; this.name = name; this.phone = phone;
            this.took = took; this.gave = gave; this.type = type;
        }
    }

    // ── onCreate ──────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        bindViews();
        setupTabs();
        setupSearch();
        setupFab();
        setupBottomNav();

        // Demo data — replace with your DB later
        allPersons.add(new Person(nextId++, "أحمد بن علي",   "0551234567", 5000, 2000, Person.TYPE_CLIENT));
        allPersons.add(new Person(nextId++, "فاطمة الزهراء", "0661234567", 1500,    0, Person.TYPE_CLIENT));
        allPersons.add(new Person(nextId++, "مورد الجملة",   "0771234567",    0, 8000, Person.TYPE_SUPPLIER));

        displayCurrentTab();
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
        EditText etTook  = makeInput("أخذت (د.ج)", true);
        EditText etGave  = makeInput("أعطيت (د.ج)", true);

        layout.addView(etName);
        layout.addView(etPhone);
        layout.addView(etTook);
        layout.addView(etGave);

        new AlertDialog.Builder(this)
                .setTitle("إضافة " + label)
                .setView(layout)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "يرجى إدخال الاسم", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double took = parseAmount(etTook.getText().toString());
                    double gave = parseAmount(etGave.getText().toString());
                    int type = isClientsTabActive ? Person.TYPE_CLIENT : Person.TYPE_SUPPLIER;
                    allPersons.add(new Person(nextId++, name, etPhone.getText().toString().trim(), took, gave, type));
                    displayCurrentTab();
                    Toast.makeText(this, "تمت الإضافة", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    // ── Edit / Delete ─────────────────────────────────────────────────
    private void showEditDeleteDialog(Person person) {
        new AlertDialog.Builder(this)
                .setTitle(person.name)
                .setItems(new String[]{"تعديل", "حذف"}, (dialog, which) -> {
                    if (which == 0) showEditDialog(person);
                    else            confirmDelete(person);
                }).show();
    }

    private void showEditDialog(Person person) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        EditText etName  = makeInput("الاسم *", false);
        EditText etPhone = makeInput("رقم الهاتف", false);
        EditText etTook  = makeInput("أخذت (د.ج)", true);
        EditText etGave  = makeInput("أعطيت (د.ج)", true);

        etName.setText(person.name);
        etPhone.setText(person.phone);
        if (person.took > 0) etTook.setText(formatRaw(person.took));
        if (person.gave > 0) etGave.setText(formatRaw(person.gave));

        layout.addView(etName);
        layout.addView(etPhone);
        layout.addView(etTook);
        layout.addView(etGave);

        new AlertDialog.Builder(this)
                .setTitle("تعديل")
                .setView(layout)
                .setPositiveButton("حفظ", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "يرجى إدخال الاسم", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    person.name  = name;
                    person.phone = etPhone.getText().toString().trim();
                    person.took  = parseAmount(etTook.getText().toString());
                    person.gave  = parseAmount(etGave.getText().toString());
                    displayCurrentTab();
                    Toast.makeText(this, "تم التعديل", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void confirmDelete(Person person) {
        new AlertDialog.Builder(this)
                .setTitle("حذف")
                .setMessage("هل تريد حذف " + person.name + "؟")
                .setPositiveButton("نعم", (dialog, which) -> {
                    allPersons.remove(person);
                    displayCurrentTab();
                    Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("لا", null)
                .show();
    }

    // ── Display ───────────────────────────────────────────────────────
    private void displayCurrentTab() {
        filterAndDisplay(etSearch.getText().toString().trim());
    }

    private void filterAndDisplay(String query) {
        int type = isClientsTabActive ? Person.TYPE_CLIENT : Person.TYPE_SUPPLIER;
        List<Person> filtered = new ArrayList<>();
        for (Person p : allPersons) {
            if (p.type != type) continue;
            if (!query.isEmpty() && !p.name.contains(query) && !p.phone.contains(query)) continue;
            filtered.add(p);
        }
        renderList(filtered);
    }

    private void renderList(List<Person> persons) {
        clientListContainer.removeAllViews();
        double totalTook = 0, totalGave = 0;
        for (Person p : persons) {
            totalTook += p.took;
            totalGave += p.gave;
            clientListContainer.addView(buildPersonRow(p));
        }
        tvTook.setText(formatAmount(totalTook));
        tvGave.setText(formatAmount(totalGave));
        tvCustomerCount.setText(isClientsTabActive
                ? "العملاء (" + persons.size() + ")"
                : "الموردين (" + persons.size() + ")");
    }

    // ── Build row ─────────────────────────────────────────────────────
    private View buildPersonRow(Person person) {
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

        // LEFT: balance
        double balance = person.took - person.gave;
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
        tvName.setText(person.name);
        tvName.setTextSize(15);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);
        tvName.setTextColor(0xFF333333);

        TextView tvPhone = new TextView(this);
        tvPhone.setText(person.phone.isEmpty() ? "—" : person.phone);
        tvPhone.setTextSize(12);
        tvPhone.setTextColor(0xFF888888);

        right.addView(tvName);
        right.addView(tvPhone);

        row.addView(left);
        row.addView(spacer);
        row.addView(right);
        card.addView(row);

        // Click → open PersonDetailActivity
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, PersonDetailActivity.class);
            intent.putExtra("person_name",  person.name);
            intent.putExtra("person_phone", person.phone);
            intent.putExtra("person_took",  person.took);
            intent.putExtra("person_gave",  person.gave);
            startActivity(intent);
        });

        // Long press → edit / delete
        card.setOnLongClickListener(v -> {
            showEditDeleteDialog(person);
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

    private double parseAmount(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private String formatAmount(double v) {
        return (v == (long) v ? (long) v : v) + CURRENCY;
    }

    private String formatRaw(double v) {
        return v == (long) v ? String.valueOf((long) v) : String.valueOf(v);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}