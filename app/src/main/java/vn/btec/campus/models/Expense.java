package vn.btec.campus.models;

import java.util.Date;

public class Expense {
    private Long id;
    private double amount;
    private String description;
    private Date date;
    private Long categoryId;
    private Long userId;
    private boolean isRecurring;
    private String recurringPeriod; // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    private String imagePath;
    private String contactTag;

    public Expense() {
    }

    public Expense(Long id, double amount, String description, Date date, 
                  Long categoryId, Long userId, boolean isRecurring, String recurringPeriod,
                  String imagePath, String contactTag) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryId = categoryId;
        this.userId = userId;
        this.isRecurring = isRecurring;
        this.recurringPeriod = recurringPeriod;
        this.imagePath = imagePath;
        this.contactTag = contactTag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getRecurringPeriod() {
        return recurringPeriod;
    }

    public void setRecurringPeriod(String recurringPeriod) {
        this.recurringPeriod = recurringPeriod;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getContactTag() {
        return contactTag;
    }

    public void setContactTag(String contactTag) {
        this.contactTag = contactTag;
    }
}
