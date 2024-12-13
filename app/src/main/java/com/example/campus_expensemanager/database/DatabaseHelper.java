package com.example.campus_expensemanager.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.campus_expensemanager.entities.Category;
import com.example.campus_expensemanager.entities.Expense;
import com.example.campus_expensemanager.entities.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên cơ sở dữ liệu và phiên bản
    public static final String DATABASE_NAME = "campus_expense_manager1.db";
    private static final int DATABASE_VERSION = 9;

    // Tên bảng và cột
    private static final String TABLE_EXPENSES = "expenses";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_CATEGORIES = "Categories";
    private static final String TABLE_SPENDING_LIMITS = "SpendingLimit";

    // Các cột trong bảng expenses
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_USERNAME = "username";

    // Các cột trong bảng users
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_FULL_NAME = "fullName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_BALANCE = "balance";

    // Các cột trong bảng categories
    private static final String COLUMN_CATEGORY_ID = "id";
    private static final String COLUMN_CATEGORY_NAME = "name";
    private static final String COLUMN_CATEGORY_DESCRIPTION = "description";
    private static final String COLUMN_DATE_CREATED = "date";

    // Các cột trong bảng spending limits
    private static final String COLUMN_SPENDING_LIMIT_ID = "id";
    public static final String COLUMN_SPENDING_LIMIT_DESCRIPTION = "description";
    public static final String COLUMN_SPENDING_LIMIT_AMOUNT = "amount";
    public static final String COLUMN_SPENDING_START_DATE = "startDate";
    public static final String COLUMN_SPENDING_END_DATE = "endDate";

    // Câu lệnh SQL để tạo bảng
    private static final String CREATE_TABLE_EXPENSES =
            "CREATE TABLE " + TABLE_EXPENSES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_TYPE + " TEXT);";

    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULL_NAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    "is_logged_in INTEGER DEFAULT 0, " +
                    COLUMN_BALANCE + " REAL DEFAULT 0);";

    private static final String CREATE_TABLE_CATEGORIES =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE_CREATED + " TEXT, " +
                    COLUMN_BALANCE + " REAL DEFAULT 0," +
                    COLUMN_USERNAME + " TEXT);";

    private static final String CREATE_TABLE_SPENDING_LIMIT =
            "CREATE TABLE " + TABLE_SPENDING_LIMITS + " (" +
                    COLUMN_SPENDING_LIMIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SPENDING_LIMIT_DESCRIPTION + " TEXT, " +
                    COLUMN_SPENDING_LIMIT_AMOUNT + " TEXT, " +
                    COLUMN_SPENDING_START_DATE + " TEXT, " +
                    COLUMN_SPENDING_END_DATE + " TEXT, " +
                    COLUMN_USERNAME + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng khi cơ sở dữ liệu được khởi tạo
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_SPENDING_LIMIT);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp cơ sở dữ liệu khi phiên bản thay đổi
        Log.d("DatabaseHelper", "onUpgrade - oldVersion: " + oldVersion + " newVersion: " + newVersion);

        // Thêm cột vào bảng users nếu cần
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN is_logged_in INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_EXPENSES + " ADD COLUMN " + COLUMN_USERNAME + " TEXT;");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_BALANCE + " REAL DEFAULT 0");
        }
        // Xóa và tạo lại bảng nếu cần
        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        }
    }

    // Phương thức thêm chi tiêu vào cơ sở dữ liệu
    public boolean addExpense(String username, double amount, String description, String date, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Thêm thông tin chi tiêu
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_TYPE, type);

        // Kiểm tra loại chi tiêu
        boolean isIncome = type.equalsIgnoreCase("Income");
        boolean expenseInserted = db.insert(TABLE_EXPENSES, null, values) != -1;

        // Cập nhật số dư nếu thêm chi tiêu thành công
        return expenseInserted && updateBalance(username, amount, isIncome);
    }

    // Phương thức cập nhật số dư của người dùng
    public boolean updateBalance(String username, double amount, boolean isIncome) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_BALANCE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") double currentBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_BALANCE));
            cursor.close();

            // Cập nhật số dư dựa trên loại chi tiêu
            double updatedBalance = isIncome ? currentBalance + amount : currentBalance - amount;

            ContentValues values = new ContentValues();
            values.put(COLUMN_BALANCE, updatedBalance);
            return db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username}) > 0;
        }
        return false;
    }

    // Phương thức lấy chi tiêu theo tên người dùng
    public Cursor getExpensesByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_USERNAME + " = ?";
        return db.rawQuery(query, new String[]{username});
    }

    // Phương thức thêm người dùng vào cơ sở dữ liệu
    public boolean insertUser(String fullName, String email, String username, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // Thêm thông tin người dùng
        contentValues.put(COLUMN_FULL_NAME, fullName);
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_PHONE, phone);

        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1; // Trả về true nếu thêm thành công
    }

    // Phương thức thêm danh mục
    public boolean addCategory(String username, String name, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Thêm thông tin danh mục
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_CATEGORY_NAME, name);
        values.put(COLUMN_CATEGORY_DESCRIPTION, description);
        values.put(COLUMN_DATE_CREATED, date);

        long result = db.insert(TABLE_CATEGORIES, null, values);
        return result != -1; // Trả về true nếu thêm thành công
    }

    // Kiểm tra thông tin đăng nhập của người dùng
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null);

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Lấy thông tin người dùng theo tên
    public Cursor getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null; // Không thực hiện truy vấn nếu username rỗng
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        return db.rawQuery(query, new String[]{username});
    }

    // Lấy danh sách danh mục theo tên người dùng
    public List<String> getCategoriesByUser(String username) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_NAME + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // Kiểm tra xem danh mục đã tồn tại chưa
    public boolean isCategoryExists(String username, String categoryName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = ? AND " + COLUMN_USERNAME + " = ?", new String[]{categoryName, username});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Cập nhật số dư của danh mục
    public boolean updateCategoryBalance(String username, String categoryName, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_BALANCE + " FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORY_NAME + " = ? AND " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{categoryName, username});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") double currentBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_BALANCE));
            cursor.close();

            double newBalance = type.equalsIgnoreCase("Income") ? currentBalance + amount : currentBalance - amount;

            ContentValues values = new ContentValues();
            values.put(COLUMN_BALANCE, newBalance);
            int rowsUpdated = db.update(TABLE_CATEGORIES, values, COLUMN_CATEGORY_NAME + " = ? AND " + COLUMN_USERNAME + " = ?", new String[]{categoryName, username});

            return rowsUpdated > 0; // Trả về true nếu cập nhật thành công
        }

        return false; // Trả về false nếu không tìm thấy danh mục
    }

    // Cập nhật mật khẩu người dùng
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USERNAME + "=?", new String[]{username});
        db.close();

        return rowsAffected > 0;
    }

    // Lấy danh sách tất cả người dùng
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, email, username, phone FROM " + TABLE_USERS, null);
    }

    // Lấy danh sách danh mục theo tên người dùng
    public List<Category> getCategoriesByUsername(String username) {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Categories WHERE username = ?", new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(COLUMN_BALANCE));
                @SuppressLint("Range") String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));

                categoryList.add(new Category(name, amount, dateCreated));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return categoryList;
    }

    // Thêm giới hạn chi tiêu
    public boolean insertSpendingLimit(String description, String amount, String startDate, String endDate, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SPENDING_LIMIT_DESCRIPTION, description);
        values.put(COLUMN_SPENDING_LIMIT_AMOUNT, amount);
        values.put(COLUMN_SPENDING_START_DATE, startDate);
        values.put(COLUMN_SPENDING_END_DATE, endDate);
        values.put(COLUMN_USERNAME, username);

        long result = db.insert(TABLE_SPENDING_LIMITS, null, values);
        return result != -1;
    }

    // Lấy giới hạn chi tiêu theo tên người dùng
    public Cursor getSpendingLimitByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_SPENDING_LIMITS + " WHERE " + COLUMN_USERNAME + " = ?";
        return db.rawQuery(query, new String[]{username});
    }
    public boolean deleteSpendingLimit(String description, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_SPENDING_LIMITS,
                COLUMN_SPENDING_LIMIT_DESCRIPTION + "=? AND " + COLUMN_USERNAME + "=?",
                new String[]{description, username});
        return rowsAffected > 0;
    }

    // Tính ngân sách ròng trong khoảng thời gian
    @SuppressLint("Range")
    public double getNetBudgetInRange(String username, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Tính tổng thu nhập
        String incomeQuery = "SELECT SUM(amount) AS total FROM expenses WHERE username = ? AND type = 'Income' AND date >= ? AND date <= ?";
        Cursor incomeCursor = db.rawQuery(incomeQuery, new String[]{username, startDate, endDate});
        double totalIncome = 0.0;

        if (incomeCursor != null && incomeCursor.moveToFirst()) {
            totalIncome = incomeCursor.getDouble(incomeCursor.getColumnIndex("total"));
            incomeCursor.close();
        }

        // Tính tổng chi tiêu
        String expenseQuery = "SELECT SUM(amount) AS total FROM expenses WHERE username = ? AND type = 'Expense' AND date >= ? AND date <= ?";
        Cursor expenseCursor = db.rawQuery(expenseQuery, new String[]{username, startDate, endDate});
        double totalExpense = 0.0;

        if (expenseCursor != null && expenseCursor.moveToFirst()) {
            totalExpense = expenseCursor.getDouble(expenseCursor.getColumnIndex("total"));
            expenseCursor.close();
        }

        // Tính ngân sách ròng
        return totalIncome - totalExpense;
    }
    public boolean updateSpendingLimit(String oldDescription, String amount, String startDate, String endDate, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SPENDING_LIMIT_DESCRIPTION, oldDescription);
        contentValues.put(COLUMN_SPENDING_LIMIT_AMOUNT, amount);
        contentValues.put(COLUMN_SPENDING_START_DATE, startDate);
        contentValues.put(COLUMN_SPENDING_END_DATE, endDate);

        int rowsAffected = db.update(TABLE_SPENDING_LIMITS, contentValues,
                COLUMN_SPENDING_LIMIT_DESCRIPTION + " = ? AND " + COLUMN_USERNAME + " = ?",
                new String[]{oldDescription, username});

        return rowsAffected > 0; // Return true if at least one row was updated
    }
    // Kiểm tra cấu trúc bảng
    public void checkTableStructure() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + TABLE_SPENDING_LIMITS + ")", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String columnName = cursor.getString(cursor.getColumnIndex("name"));
                Log.d("DatabaseInfo", "Column name: " + columnName);
            }
            cursor.close();
        }
    }

    // Xóa cơ sở dữ liệu
    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
    public boolean deleteCategory(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("categories", "id = ?", new String[]{String.valueOf(categoryId)}) > 0;
    }
    //xoa du lieu trong data
//    private void deleteCategoryFromDatabase(Category category) {
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        dbHelper.deleteCategory(category.getName(), category.getUsername());
//    }
//
//    private void updateCategoryInDatabase(Category category, String newName) {
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        dbHelper.updateCategoryName(category.getUsername(), category.getName(), newName);
//    }
}
//public boolean checkUserExists(String usernameOrEmail) {
//    SQLiteDatabase db = this.getReadableDatabase();
//    String query = "SELECT * FROM " + USER_TABLE + " WHERE " + COLUMN_USERNAME + " = ? OR " + COLUMN_EMAIL + " = ?";
//    Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail});
//
//    boolean userExists = cursor != null && cursor.moveToFirst();
//    cursor.close();
//    return userExists;
//}
