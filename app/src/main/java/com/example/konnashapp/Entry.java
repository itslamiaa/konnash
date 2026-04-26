package com.example.konnashapp;

public class Entry {
    private String title;
    private double amount;
    private boolean isExpense;

    public Entry(String title, double amount, boolean isExpense) {
        this.title = title;
        this.amount = amount;
        this.isExpense = isExpense;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isExpense() {
        return isExpense;
    }
}