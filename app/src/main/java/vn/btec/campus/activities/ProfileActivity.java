package vn.btec.campus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import vn.btec.campus.R;
import vn.btec.campus.utils.LanguageUtils;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout budgetSettingsLayout;
    private LinearLayout notificationsLayout;
    private LinearLayout languageSettingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupClickListeners() {
        // Budget Settings click listener
        findViewById(R.id.budgetSettingsLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, BudgetSettingsActivity.class));
        });

        // Language Settings click listener
        languageSettingsLayout = findViewById(R.id.languageSettingsLayout);
        languageSettingsLayout.setOnClickListener(v -> {
            showLanguageDialog();
        });

        // Notifications click listener
        notificationsLayout = findViewById(R.id.notificationsLayout);
        notificationsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Tiếng Việt"};
        String currentLang = LanguageUtils.getCurrentLanguage((LoginActivity) getApplicationContext());
        int selectedIndex = currentLang.equals("en") ? 0 : 1;

        new AlertDialog.Builder(this)
                .setTitle(R.string.language)
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String selectedLang = which == 0 ? "en" : "vi";
                    if (!selectedLang.equals(currentLang)) {
                        LanguageUtils.setLocale(this, selectedLang);
                        recreate();
                    }
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
