package vn.btec.campus.models;

import java.util.Date;

public class RecurringExpense {
    private long id;
    private String name;
    private double amount;
    private String frequency;
    private Date dueDate;
    private int reminderDays;
    private String category;

    public RecurringExpense() {
    }

    public RecurringExpense(String name, double amount, String frequency, Date dueDate, int reminderDays, String category) {
        this.name = name;
        this.amount = amount;
        this.frequency = frequency;
        this.dueDate = dueDate;
        this.reminderDays = reminderDays;
        this.category = category;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getReminderDays() {
        return reminderDays;
    }

    public void setReminderDays(int reminderDays) {
        this.reminderDays = reminderDays;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
