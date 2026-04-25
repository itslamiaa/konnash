package com.example.konnashapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class IncomeActivity extends AppCompatActivity {

    TextView tvAmount, tvExpression;
    String currentInput = "0";
    String operator = "";
    double memory = 0;
    double firstValue = 0;
    boolean isOperatorClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        tvAmount = findViewById(R.id.tvAmount);
        tvExpression = findViewById(R.id.tvExpression);

        // أزرار الأرقام
        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9};
        String[] nums = {"0","1","2","3","4","5","6","7","8","9"};

        for (int i = 0; i < numIds.length; i++) {
            final String num = nums[i];
            findViewById(numIds[i]).setOnClickListener(v -> appendNumber(num));
        }

        // النقطة العشرية
        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (!currentInput.contains(".")) {
                currentInput += ".";
                updateDisplay();
            }
        });

        // العمليات
        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("/"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            double val = Double.parseDouble(currentInput);
            currentInput = String.valueOf(val / 100);
            updateDisplay();
        });

        // يساوي
        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());

        // AC - مسح الكل
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            currentInput = "0";
            operator = "";
            firstValue = 0;
            isOperatorClicked = false;
            tvExpression.setText("");
            updateDisplay();
        });

        // Backspace
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (currentInput.length() > 1) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            } else {
                currentInput = "0";
            }
            updateDisplay();
        });

        // M+ و M-
        findViewById(R.id.btnMPlus).setOnClickListener(v -> {
            memory += Double.parseDouble(currentInput);
        });
        findViewById(R.id.btnMMinus).setOnClickListener(v -> {
            memory -= Double.parseDouble(currentInput);
        });

        // زر تأكيد
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, SuccessActivity.class);
            intent.putExtra("amount", currentInput + ",00");
            startActivity(intent);
        });

        // زر الرجوع
        findViewById(R.id.tvBack).setOnClickListener(v -> finish());
    }

    void appendNumber(String num) {
        if (isOperatorClicked) {
            currentInput = num;
            isOperatorClicked = false;
        } else {
            if (currentInput.equals("0")) {
                currentInput = num;
            } else {
                currentInput += num;
            }
        }

        // كي تكتب الرقم الثاني تحسب النتيجة وتبانها في الصغيرة
        if (!operator.isEmpty()) {
            try {
                double secondValue = Double.parseDouble(currentInput);
                double result = 0;
                switch (operator) {
                    case "+": result = firstValue + secondValue; break;
                    case "-": result = firstValue - secondValue; break;
                    case "×": result = firstValue * secondValue; break;
                    case "/":
                        if (secondValue != 0) result = firstValue / secondValue;
                        break;
                }
                String resultStr = result == (long) result ?
                        String.valueOf((long) result) : String.valueOf(result);
                tvExpression.setText((long)firstValue + " " + operator + " " + currentInput + " = " + resultStr + ",00");
            } catch (Exception e) {
                // ignore
            }
        }

        updateDisplay();
    }

    void setOperator(String op) {
        firstValue = Double.parseDouble(currentInput);
        operator = op;
        // تبان العملية في الصغيرة مباشرة
        tvExpression.setText(currentInput + " " + op);
        isOperatorClicked = true;
    }
    void calculate() {
        if (operator.isEmpty()) return;
        double secondValue = Double.parseDouble(currentInput);
        double result = 0;

        switch (operator) {
            case "+": result = firstValue + secondValue; break;
            case "-": result = firstValue - secondValue; break;
            case "×": result = firstValue * secondValue; break;
            case "/":
                if (secondValue != 0) result = firstValue / secondValue;
                break;
        }

        // العملية الحسابية كاملة في الصغيرة
        String expression = (long)firstValue + " " + operator + " " + currentInput + " = ";

        if (result == (long) result) {
            currentInput = String.valueOf((long) result);
        } else {
            currentInput = String.valueOf(result);
        }

        // النتيجة + ,00 في الصغيرة
        tvExpression.setText(expression + currentInput + ",00");

        operator = "";
        isOperatorClicked = false;

        // النتيجة الكبيرة
        updateDisplay();
    }
    void updateDisplay() {
        tvAmount.setText(currentInput);
    }
}