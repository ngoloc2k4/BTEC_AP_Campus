package com.example.campus_expensemanager.entities;

public class Category {

    private String name;
    private double amount;
    private String dateCreated;
    private String type;
    private String date;


    public Category( String name, double amount, String dateCreated) {
        this.name = name;
        this.amount = amount;
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
