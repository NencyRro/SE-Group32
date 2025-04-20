package com.qmul.financetracker;

import java.time.LocalDate;

public class Transaction {
    private final LocalDate date;
    private final String category;
    private final double amount;
    private final String note;

    public Transaction(String dateStr, String category, double amount, String note) {
        this.date = LocalDate.parse(dateStr);
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
}