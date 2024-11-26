package vn.btec.campus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.btec.campus.models.User;
import vn.btec.campus.database.DatabaseManager;

public class SessionManager {
    private static final String PREF_NAME = "CampusPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILE_PIC = "profilePic";
    private static final String KEY_REGISTERED_USERS = "registered_users";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_BUDGET_LIMIT = "budget_limit";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_BUDGET_THRESHOLD = "budget_threshold";
    private static final String KEY_BUDGET_ALERTS_ENABLED = "budget_alerts_enabled";
    private static final String KEY_EXPENSE_REMINDERS_ENABLED = "expense_reminders_enabled";
    private static final String KEY_NOTIFICATION_TIME = "notification_time";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private DatabaseManager databaseManager;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        databaseManager = new DatabaseManager(context);
    }

    public void createLoginSession(String email, boolean rememberMe) {
        try {
            JSONArray users = new JSONArray(pref.getString(KEY_REGISTERED_USERS, "[]"));
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.putString(KEY_EMAIL, email);
                    editor.putString(KEY_USERNAME, user.getString("username"));
                    editor.putString(KEY_CURRENT_USER_ID, user.getString("id"));
                    editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
                    editor.commit();
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean validateLogin(String email, String password) {
        databaseManager.open();
        boolean isValid = databaseManager.validateUserCredentials(email, password);
        databaseManager.close();
        return isValid;
    }

    public boolean registerUser(User user, String password) {
        try {
            // Check if email already exists in database
            databaseManager.open();
            User existingUser = databaseManager.getUser(user.getEmail());
            if (existingUser != null) {
                databaseManager.close();
                return false; // Email already exists
            }

            // Set the password before creating the user
            user.setPassword(password);
            
            // Store user in database
            long userId = databaseManager.createUser(user);
            databaseManager.close();

            if (userId == -1) {
                return false; // Database insertion failed
            }

            // Update user object with generated ID
            user.setId(userId);

            // Store minimal user info in SharedPreferences
            JSONArray users = new JSONArray(pref.getString(KEY_REGISTERED_USERS, "[]"));
            JSONObject newUser = new JSONObject();
            newUser.put("id", userId);
            newUser.put("username", user.getUsername());
            newUser.put("email", user.getEmail());

            users.put(newUser);
            editor.putString(KEY_REGISTERED_USERS, users.toString());
            editor.commit();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        String registeredUsers = pref.getString(KEY_REGISTERED_USERS, "[]");
        editor.clear();
        editor.putString(KEY_REGISTERED_USERS, registeredUsers);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isRememberMeEnabled() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getCurrentUserId() {
        return pref.getString(KEY_CURRENT_USER_ID, null);
    }

    // Budget related methods
    public void setBudgetLimit(double limit) {
        editor.putFloat(KEY_BUDGET_LIMIT, (float) limit);
        editor.commit();
    }

    public double getBudgetLimit() {
        return pref.getFloat(KEY_BUDGET_LIMIT, 0.0f);
    }

    public void setNotificationEnabled(boolean enabled) {
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled);
        editor.commit();
    }

    public boolean isNotificationEnabled() {
        return pref.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }

    public void setBudgetThreshold(String threshold) {
        editor.putString(KEY_BUDGET_THRESHOLD, threshold);
        editor.commit();
    }

    public String getBudgetThreshold() {
        return pref.getString(KEY_BUDGET_THRESHOLD, "85");
    }

    // New notification and alert methods
    public void setBudgetAlertsEnabled(boolean enabled) {
        editor.putBoolean(KEY_BUDGET_ALERTS_ENABLED, enabled);
        editor.commit();
    }

    public boolean getBudgetAlertsEnabled() {
        return pref.getBoolean(KEY_BUDGET_ALERTS_ENABLED, true);
    }

    public void setExpenseRemindersEnabled(boolean enabled) {
        editor.putBoolean(KEY_EXPENSE_REMINDERS_ENABLED, enabled);
        editor.commit();
    }

    public boolean getExpenseRemindersEnabled() {
        return pref.getBoolean(KEY_EXPENSE_REMINDERS_ENABLED, true);
    }

    public void setNotificationTime(String time) {
        editor.putString(KEY_NOTIFICATION_TIME, time);
        editor.commit();
    }

    public String getNotificationTime() {
        return pref.getString(KEY_NOTIFICATION_TIME, "09:00"); // Default to 9:00 AM
    }
}
