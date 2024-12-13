package com.example.campus_expensemanager.entities;

public class SpendingLimit {
    private String amount;
    private String description;
    private String duration;

    public SpendingLimit(String amount, String description, String duration) {
        this.amount = amount;
        this.description = description;
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }
}
