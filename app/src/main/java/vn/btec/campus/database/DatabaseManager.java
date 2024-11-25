package vn.btec.campus.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.btec.campus.models.Category;
import vn.btec.campus.models.Expense;
import vn.btec.campus.models.Income;
import vn.btec.campus.models.User;
import vn.btec.campus.utils.PasswordUtils;

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private boolean isOpen = false;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public synchronized void open() throws SQLiteException {
        if (!isOpen) {
            database = dbHelper.getWritableDatabase();
            isOpen = true;
        }
    }

    public synchronized void close() {
        if (isOpen) {
            dbHelper.close();
            isOpen = false;
        }
    }

    private void ensureOpen() {
        if (!isOpen) {
            open();
        }
    }

    // User Operations
    public long createUser(User user) {
        try {
            ensureOpen();
            
            // Generate salt and hash password
            byte[] salt = PasswordUtils.generateSalt();
            String hashedPassword = PasswordUtils.hashPassword(user.getPassword(), salt);
            String saltBase64 = PasswordUtils.bytesToBase64(salt);

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword);
            values.put(DatabaseHelper.COLUMN_PASSWORD_SALT, saltBase64);
            values.put(DatabaseHelper.COLUMN_PROFILE_PIC, user.getProfilePic());

            return database.insert(DatabaseHelper.TABLE_USERS, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error creating user: " + e.getMessage());
            return -1;
        }
    }

    @SuppressLint("Range")
    public User getUser(String email) {
        try {
            ensureOpen();
            
            Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
            );

            User user = null;
            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD)));
                user.setProfilePic(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROFILE_PIC)));
            }
            if (cursor != null) {
                cursor.close();
            }
            return user;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting user: " + e.getMessage());
            return null;
        }
    }

    @SuppressLint("Range")
    public boolean validateUserCredentials(String email, String password) {
        Cursor cursor = null;
        try {
            ensureOpen();
            
            cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_PASSWORD, DatabaseHelper.COLUMN_PASSWORD_SALT},
                DatabaseHelper.COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
                String saltBase64 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD_SALT));
                byte[] salt = PasswordUtils.base64ToBytes(saltBase64);
                return PasswordUtils.verifyPassword(password, storedHash, salt);
            }
            return false;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error validating credentials: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Category Operations
    public long createCategory(Category category) {
        try {
            ensureOpen();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.getName());
            values.put(DatabaseHelper.COLUMN_CATEGORY_TYPE, category.getType());
            values.put(DatabaseHelper.COLUMN_USER_ID, category.getUserId());

            return database.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error creating category: " + e.getMessage());
            return -1;
        }
    }

    @SuppressLint("Range")
    public List<Category> getCategoriesByType(long userId, String type) {
        List<Category> categories = new ArrayList<>();
        Cursor cursor = null;
        try {
            ensureOpen();
            
            cursor = database.query(
                DatabaseHelper.TABLE_CATEGORIES,
                null,
                DatabaseHelper.COLUMN_USER_ID + "=? AND " + DatabaseHelper.COLUMN_CATEGORY_TYPE + "=?",
                new String[]{String.valueOf(userId), type},
                null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    category.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_NAME)));
                    category.setType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_TYPE)));
                    category.setUserId(String.valueOf(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID))));
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting categories: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return categories;
    }

    // Expense Operations
    public long createExpense(Expense expense) {
        try {
            ensureOpen();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_AMOUNT, expense.getAmount());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, expense.getDescription());
            values.put(DatabaseHelper.COLUMN_DATE, DATE_FORMAT.format(expense.getDate()));
            values.put(DatabaseHelper.COLUMN_CATEGORY_ID, expense.getCategoryId());
            values.put(DatabaseHelper.COLUMN_USER_ID, expense.getUserId());
            values.put(DatabaseHelper.COLUMN_IMAGE_PATH, expense.getImagePath());
            values.put(DatabaseHelper.COLUMN_CONTACT_TAG, expense.getContactTag());

            return database.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error creating expense: " + e.getMessage());
            return -1;
        }
    }

    public List<Expense> getExpenses(long userId, String startDate, String endDate) {
        List<Expense> expenses = new ArrayList<>();
        Cursor cursor = null;
        try {
            ensureOpen();
            
            String selection = DatabaseHelper.COLUMN_USER_ID + "=? AND " +
                    DatabaseHelper.COLUMN_DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {String.valueOf(userId), startDate, endDate};

            cursor = database.query(
                DatabaseHelper.TABLE_EXPENSES,
                null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Expense expense = new Expense();
                    expense.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    expense.setAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT)));
                    expense.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
                    expense.setDate(parseDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))));
                    expense.setCategoryId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID)));
                    expense.setUserId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)));
                    expense.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)));
                    expense.setContactTag(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTACT_TAG)));
                    expenses.add(expense);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting expenses: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return expenses;
    }

    // Income Operations
    public long createIncome(Income income) {
        try {
            ensureOpen();
            
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_AMOUNT, income.getAmount());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, income.getDescription());
            values.put(DatabaseHelper.COLUMN_DATE, DATE_FORMAT.format(income.getDate()));
            values.put(DatabaseHelper.COLUMN_CATEGORY_ID, income.getCategoryId());
            values.put(DatabaseHelper.COLUMN_USER_ID, income.getUserId());
            values.put(DatabaseHelper.COLUMN_IMAGE_PATH, income.getImagePath());
            values.put(DatabaseHelper.COLUMN_CONTACT_TAG, income.getContactTag());

            return database.insert(DatabaseHelper.TABLE_INCOME, null, values);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error creating income: " + e.getMessage());
            return -1;
        }
    }

    public List<Income> getIncome(long userId, String startDate, String endDate) {
        List<Income> incomeList = new ArrayList<>();
        Cursor cursor = null;
        try {
            ensureOpen();
            
            String selection = DatabaseHelper.COLUMN_USER_ID + "=? AND " +
                    DatabaseHelper.COLUMN_DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {String.valueOf(userId), startDate, endDate};

            cursor = database.query(
                DatabaseHelper.TABLE_INCOME,
                null, selection, selectionArgs, null, null, DatabaseHelper.COLUMN_DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Income income = new Income();
                    income.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
                    income.setAmount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT)));
                    income.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION)));
                    income.setDate(parseDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))));
                    income.setCategoryId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID)));
                    income.setUserId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID)));
                    income.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_PATH)));
                    income.setContactTag(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTACT_TAG)));
                    incomeList.add(income);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting income: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return incomeList;
    }

    // Statistics and Reports
    public double getTotalExpenses(long userId, String startDate, String endDate) {
        String[] columns = {"SUM(" + DatabaseHelper.COLUMN_AMOUNT + ") as total"};
        String selection = DatabaseHelper.COLUMN_USER_ID + "=? AND " +
                DatabaseHelper.COLUMN_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {String.valueOf(userId), startDate, endDate};

        Cursor cursor = null;
        try {
            ensureOpen();
            
            cursor = database.query(
                DatabaseHelper.TABLE_EXPENSES,
                columns, selection, selectionArgs, null, null, null
            );

            double total = 0;
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting total expenses: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    public double getTotalIncome(long userId, String startDate, String endDate) {
        String[] columns = {"SUM(" + DatabaseHelper.COLUMN_AMOUNT + ") as total"};
        String selection = DatabaseHelper.COLUMN_USER_ID + "=? AND " +
                DatabaseHelper.COLUMN_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {String.valueOf(userId), startDate, endDate};

        Cursor cursor = null;
        try {
            ensureOpen();
            
            cursor = database.query(
                DatabaseHelper.TABLE_INCOME,
                columns, selection, selectionArgs, null, null, null
            );

            double total = 0;
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting total income: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return 0;
    }

    private Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + dateStr, e);
            return new Date(); // Return current date as fallback
        }
    }
}
