package com.example.campus_expensemanager.entities;

public class Expense {
    private int amount;
    private String description;
    private String date;
    private String type;
    private String category;

    public Expense(int amount, String description, String date, String type, String category) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
        this.category = category;
    }

    // Getter methods for each field
    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

//    public Object getId() {return
//    }
}
