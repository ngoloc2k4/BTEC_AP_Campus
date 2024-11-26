package vn.btec.campus;

import android.app.Application;
import android.content.Context;

import vn.btec.campus.utils.LanguageUtils;

public class CampusApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        // Apply saved language before attaching base context
        String language = LanguageUtils.getSelectedLanguage(base);
        super.attachBaseContext(LanguageUtils.updateResources(base, language));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize language
        String language = LanguageUtils.getSelectedLanguage(this);
        LanguageUtils.setLocale(this, language);
    }
}
