package com.example.campus_expensemanager.entities;

public class User {
    private String fullName;
    private String userName;

    public User(String fullName, String userName) {
        this.fullName = fullName;
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }
}
