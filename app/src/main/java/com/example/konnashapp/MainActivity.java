package com.example.konnashapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // ---- Screens ----
    private static final int SCREEN_DEBT = 0;   // دفتر الديون
    private static final int SCREEN_CASH = 1;   // دفتر النقدية
    private int currentScreen = SCREEN_DEBT;

    // ---- Buttons Screen 1 — دفتر الديون ----
    private Button btnAddCustomer, btnAddSupplier;
    private LinearLayout debtButtonsLayout;

    // ---- Buttons Screen 2 — دفتر النقدية ----
    private Button btnExpense, btnIncome;
    private LinearLayout cashButtonsLayout;

    // ---- Shared center ----
    private ImageView bookLogo;
    private TextView descriptionText;

    // ---- Bottom Nav ----
    private LinearLayout navMore, navCashBook, navDebtBook;
    private ImageView moreIcon, walletIcon, bookIcon;
    private TextView moreLabel, cashLabel, debtLabel;

    // ---- Root layout (for background color change) ----
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClickListeners();

        // Default screen = دفتر الديون
        showScreen(SCREEN_DEBT);
    }

    // =========================================================
    //  INIT
    // =========================================================
    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);

        // Screen 1
        btnAddCustomer    = findViewById(R.id.btnAddCustomer);
        btnAddSupplier    = findViewById(R.id.btnAddSupplier);
        debtButtonsLayout = findViewById(R.id.debtButtonsLayout);

        // Screen 2
        btnExpense        = findViewById(R.id.btnExpense);
        btnIncome         = findViewById(R.id.btnIncome);
        cashButtonsLayout = findViewById(R.id.cashButtonsLayout);

        // Shared
        bookLogo          = findViewById(R.id.bookLogo);
        descriptionText   = findViewById(R.id.descriptionText);

        // Bottom nav
        navMore           = findViewById(R.id.navMore);
        navCashBook       = findViewById(R.id.navCashBook);
        navDebtBook       = findViewById(R.id.navDebtBook);
        moreIcon          = findViewById(R.id.moreIcon);
        walletIcon        = findViewById(R.id.walletIcon);
        bookIcon          = findViewById(R.id.bookIcon);
        moreLabel         = findViewById(R.id.moreLabel);
        cashLabel         = findViewById(R.id.cashLabel);
        debtLabel         = findViewById(R.id.debtLabel);
    }

    // =========================================================
    //  CLICK LISTENERS
    // =========================================================
    private void setupClickListeners() {

        // ---- Bottom Nav ----
        navDebtBook.setOnClickListener(v -> showScreen(SCREEN_DEBT));
        navCashBook.setOnClickListener(v -> showScreen(SCREEN_CASH));
        navMore.setOnClickListener(v ->
                Toast.makeText(this, "المزيد — قريباً", Toast.LENGTH_SHORT).show()
        );

        // ---- Screen 1 : دفتر الديون ----
        btnAddSupplier.setOnClickListener(v ->
                Toast.makeText(this, "إضافة مورد جديد", Toast.LENGTH_SHORT).show()
        );
        btnAddCustomer.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
            startActivity(intent);
        });

        // ---- Screen 2 : دفتر النقدية ----
        // مصروف → white background (no color change)
        btnExpense.setOnClickListener(v ->
                Toast.makeText(this, "تسجيل مصروف جديد", Toast.LENGTH_SHORT).show()
        );

        // دخل → white background (no color change)
        btnIncome.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, IncomeActivity.class);
            startActivity(intent);
        });
    }

    // =========================================================
    //  SCREEN SWITCH
    // =========================================================
    private void showScreen(int screen) {
        currentScreen = screen;

        if (screen == SCREEN_DEBT) {

            // Show debt buttons, hide cash buttons
            debtButtonsLayout.setVisibility(View.VISIBLE);
            cashButtonsLayout.setVisibility(View.GONE);

            // Logo = book_logo.jpg (دفتر الديون)
            bookLogo.setImageResource(R.drawable.book_logo);

            // Description
            descriptionText.setText("هنا يمكنك تسجيل جميع ديون العملاء و الموردين");

            // Background white
            rootLayout.setBackgroundColor(Color.parseColor("#F8FAFC"));

        } else if (screen == SCREEN_CASH) {

            // Show cash buttons, hide debt buttons
            debtButtonsLayout.setVisibility(View.GONE);
            cashButtonsLayout.setVisibility(View.VISIBLE);

            // Logo = page_logo2.jpg (دفتر النقدية — different logo)
            bookLogo.setImageResource(R.drawable.page_logo2);

            // Description
            descriptionText.setText("هنا يمكنك تسجيل جميع المصروفات و المداخيل اليومية");

            // Background WHITE like debt screen
            rootLayout.setBackgroundColor(Color.parseColor("#F8FAFC"));
        }

        updateBottomNav(screen);
    }

    // =========================================================
    //  BOTTOM NAV COLORS
    // =========================================================
    private void updateBottomNav(int activeScreen) {
        // Reset all grey
        setNavColor(moreIcon,   moreLabel,   "#9CA3AF");
        setNavColor(walletIcon, cashLabel,   "#9CA3AF");
        setNavColor(bookIcon,   debtLabel,   "#9CA3AF");

        // Highlight active in blue
        switch (activeScreen) {
            case SCREEN_DEBT:
                // دفتر الديون uses wallet_icon
                setNavColor(walletIcon, debtLabel, "#3B82F6");
                break;
            case SCREEN_CASH:
                // دفتر النقدية uses book_icon
                setNavColor(bookIcon, cashLabel, "#3B82F6");
                break;
            default:
                setNavColor(moreIcon, moreLabel, "#3B82F6");
                break;
        }
    }

    private void setNavColor(ImageView icon, TextView label, String hex) {
        int color = Color.parseColor(hex);
        // Don't apply colorFilter on jpeg/png icons — it hides them
        // Only change the text label color
        if (label != null) label.setTextColor(color);
    }
}