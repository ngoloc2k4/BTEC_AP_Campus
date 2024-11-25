package vn.btec.campus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "campus.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_INCOME = "income";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Users Table Columns
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PASSWORD_SALT = "password_salt";
    public static final String COLUMN_PROFILE_PIC = "profile_pic";

    // Categories Table Columns
    public static final String COLUMN_CATEGORY_NAME = "name";
    public static final String COLUMN_CATEGORY_TYPE = "type"; // 'expense' or 'income'
    public static final String COLUMN_CATEGORY_ICON = "icon";
    public static final String COLUMN_CATEGORY_COLOR = "color";

    // Expenses Table Columns
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CONTACT_TAG = "contact_tag";

    // Income Table Columns
    // Uses same columns as expenses table

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT NOT NULL UNIQUE,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_PASSWORD_SALT + " TEXT NOT NULL,"
            + COLUMN_PROFILE_PIC + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // Create Categories Table
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CATEGORY_NAME + " TEXT NOT NULL,"
            + COLUMN_CATEGORY_TYPE + " TEXT NOT NULL,"
            + COLUMN_CATEGORY_ICON + " TEXT,"
            + COLUMN_CATEGORY_COLOR + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // Create Expenses Table
    private static final String CREATE_TABLE_EXPENSES = "CREATE TABLE " + TABLE_EXPENSES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_DATE + " DATE NOT NULL,"
            + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
            + COLUMN_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_CONTACT_TAG + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    // Create Income Table
    private static final String CREATE_TABLE_INCOME = "CREATE TABLE " + TABLE_INCOME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_DATE + " DATE NOT NULL,"
            + COLUMN_CATEGORY_ID + " INTEGER NOT NULL,"
            + COLUMN_USER_ID + " INTEGER NOT NULL,"
            + COLUMN_IMAGE_PATH + " TEXT,"
            + COLUMN_CONTACT_TAG + " TEXT,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_EXPENSES);
        db.execSQL(CREATE_TABLE_INCOME);

        // Insert default categories
        insertDefaultCategories(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // Create tables again
        onCreate(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        // Expense Categories
        String[] expenseCategories = {"Utilities", "Rent", "Shopping", "Food", "Drinks", "Travel", "House", "Car"};
        for (String category : expenseCategories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "expense");
            db.insert(TABLE_CATEGORIES, null, values);
        }

        // Income Categories
        String[] incomeCategories = {"Salary", "Bonus", "Parents", "Investment", "Other"};
        for (String category : incomeCategories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, category);
            values.put(COLUMN_CATEGORY_TYPE, "income");
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }
}
