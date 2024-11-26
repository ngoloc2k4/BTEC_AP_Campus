package vn.btec.campus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageUtils {
    private static final String SELECTED_LANGUAGE = "selected_language";

    // Set the locale based on the language code
    public static void setLocale(Context context, String languageCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECTED_LANGUAGE, languageCode);
        editor.apply();

        updateResources(context, languageCode);
    }

    // Get the currently selected language
    public static String getSelectedLanguage(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, "en"); // Default to English
    }

    // Update resources for the given language
    public static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        context = context.createConfigurationContext(config);
        return context;
    }

    // Use getSelectedLanguage for getting the current language
    public static String getCurrentLanguage(Context context) {
        return getSelectedLanguage(context);
    }
}
