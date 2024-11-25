package vn.btec.campus.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.btec.campus.models.RecurringExpense;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpenseManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_RECURRING_EXPENSES = "recurring_expenses";
    private static final String TABLE_EXPENSES = "expenses";

    // Common Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_FREQUENCY = "frequency";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_REMINDER_DAYS = "reminder_days";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DATE = "date";

    // Create Table Queries
    private static final String CREATE_TABLE_RECURRING_EXPENSES = "CREATE TABLE " + TABLE_RECURRING_EXPENSES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_FREQUENCY + " TEXT NOT NULL,"
            + COLUMN_DUE_DATE + " TEXT NOT NULL,"
            + COLUMN_REMINDER_DAYS + " INTEGER NOT NULL,"
            + COLUMN_CATEGORY + " TEXT NOT NULL"
            + ")";

    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_CATEGORY + " TEXT NOT NULL,"
            + COLUMN_DATE + " TEXT NOT NULL"
            + ")";

    private final SimpleDateFormat dateFormat;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RECURRING_EXPENSES);
        db.execSQL(CREATE_TABLE_EXPENSES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Recurring Expenses Methods
    public long addRecurringExpense(RecurringExpense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, expense.getName());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_FREQUENCY, expense.getFrequency());
        values.put(COLUMN_DUE_DATE, dateFormat.format(expense.getDueDate()));
        values.put(COLUMN_REMINDER_DAYS, expense.getReminderDays());
        values.put(COLUMN_CATEGORY, expense.getCategory());

        long id = db.insert(TABLE_RECURRING_EXPENSES, null, values);
        db.close();
        return id;
    }

    public List<RecurringExpense> getAllRecurringExpenses() {
        List<RecurringExpense> expenseList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECURRING_EXPENSES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                RecurringExpense expense = new RecurringExpense();
                expense.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                expense.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setFrequency(cursor.getString(cursor.getColumnIndex(COLUMN_FREQUENCY)));
                try {
                    expense.setDueDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                expense.setReminderDays(cursor.getInt(cursor.getColumnIndex(COLUMN_REMINDER_DAYS)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));

                expenseList.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenseList;
    }

    public double getCurrentMonthSpending() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = dateFormat.format(calendar.getTime());
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = dateFormat.format(calendar.getTime());

        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        // Get regular expenses
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?";
        Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate});

        if (cursor.moveToFirst()) {
            total += cursor.getDouble(0);
        }
        cursor.close();

        // Get recurring expenses
        query = "SELECT " + COLUMN_AMOUNT + ", " + COLUMN_FREQUENCY + ", " + COLUMN_DUE_DATE +
                " FROM " + TABLE_RECURRING_EXPENSES;
        cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT));
                String frequency = cursor.getString(cursor.getColumnIndex(COLUMN_FREQUENCY));
                String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE));

                // Add recurring expense amount based on frequency
                if (frequency.equals("Monthly")) {
                    total += amount;
                }
                // Add other frequency calculations as needed
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return total;
    }

    public void deleteRecurringExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECURRING_EXPENSES, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateRecurringExpense(RecurringExpense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, expense.getName());
        values.put(COLUMN_AMOUNT, expense.getAmount());
        values.put(COLUMN_FREQUENCY, expense.getFrequency());
        values.put(COLUMN_DUE_DATE, dateFormat.format(expense.getDueDate()));
        values.put(COLUMN_REMINDER_DAYS, expense.getReminderDays());
        values.put(COLUMN_CATEGORY, expense.getCategory());

        db.update(TABLE_RECURRING_EXPENSES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(expense.getId())});
        db.close();
    }
}
