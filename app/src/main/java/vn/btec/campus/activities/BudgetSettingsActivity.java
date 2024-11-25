package vn.btec.campus.activities;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.btec.campus.R;
import vn.btec.campus.utils.SessionManager;

public class BudgetSettingsActivity extends AppCompatActivity {

    private TextInputLayout tilBudgetLimit;
    private TextInputEditText etBudgetLimit;
    private SwitchMaterial switchBudgetAlert;
    private RadioGroup rgThreshold;
    private MaterialButton btnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_settings);

        sessionManager = new SessionManager(this);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        tilBudgetLimit = findViewById(R.id.tilBudgetLimit);
        etBudgetLimit = findViewById(R.id.etBudgetLimit);
        switchBudgetAlert = findViewById(R.id.switchBudgetAlert);
        rgThreshold = findViewById(R.id.rgThreshold);
        btnSave = findViewById(R.id.btnSave);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set up input filter for decimal numbers
        etBudgetLimit.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(10, 2) });

        // Load current settings
        loadCurrentSettings();

        // Set up save button
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadCurrentSettings() {
        // Load budget limit
        double budgetLimit = sessionManager.getBudgetLimit();
        if (budgetLimit > 0) {
            etBudgetLimit.setText(String.format("%.2f", budgetLimit));
        }

        // Load notification settings
        switchBudgetAlert.setChecked(sessionManager.isNotificationEnabled());

        // Load threshold setting (default is 85%)
        String threshold = sessionManager.getBudgetThreshold();
        if (threshold != null) {
            switch (threshold) {
                case "75":
                    ((RadioButton) findViewById(R.id.rb75)).setChecked(true);
                    break;
                case "85":
                    ((RadioButton) findViewById(R.id.rb85)).setChecked(true);
                    break;
                case "95":
                    ((RadioButton) findViewById(R.id.rb95)).setChecked(true);
                    break;
            }
        }
    }

    private void saveSettings() {
        // Validate budget limit
        String budgetLimitStr = etBudgetLimit.getText().toString().trim();
        if (budgetLimitStr.isEmpty()) {
            tilBudgetLimit.setError("Please enter a budget limit");
            return;
        }

        try {
            double budgetLimit = Double.parseDouble(budgetLimitStr);
            if (budgetLimit <= 0) {
                tilBudgetLimit.setError("Budget limit must be greater than 0");
                return;
            }
            sessionManager.setBudgetLimit(budgetLimit);
            tilBudgetLimit.setError(null);
        } catch (NumberFormatException e) {
            tilBudgetLimit.setError("Invalid budget amount");
            return;
        }

        // Save notification setting
        sessionManager.setNotificationEnabled(switchBudgetAlert.isChecked());

        // Save threshold setting
        int selectedThresholdId = rgThreshold.getCheckedRadioButtonId();
        String threshold = "85"; // default
        if (selectedThresholdId == R.id.rb75) {
            threshold = "75";
        } else if (selectedThresholdId == R.id.rb85) {
            threshold = "85";
        } else if (selectedThresholdId == R.id.rb95) {
            threshold = "95";
        }
        sessionManager.setBudgetThreshold(threshold);

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Input filter for decimal numbers
    private class DecimalDigitsInputFilter implements InputFilter {
        private Pattern pattern;
        private final int maxDigitsBeforeDecimal;
        private final int maxDigitsAfterDecimal;

        DecimalDigitsInputFilter(int maxDigitsBeforeDecimal, int maxDigitsAfterDecimal) {
            this.maxDigitsBeforeDecimal = maxDigitsBeforeDecimal;
            this.maxDigitsAfterDecimal = maxDigitsAfterDecimal;
            pattern = Pattern.compile("[0-9]*+((\\.[0-9]?)?)|(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                 Spanned dest, int dstart, int dend) {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.subSequence(0, dstart) + replacement + 
                          dest.subSequence(dend, dest.length());

            Matcher matcher = pattern.matcher(newVal);
            if (!matcher.matches()) return "";

            // Check digits before decimal
            int dotPos = newVal.indexOf(".");
            if (dotPos >= 0) {
                if (dotPos > maxDigitsBeforeDecimal) return "";
                // Check digits after decimal
                if (newVal.length() - dotPos - 1 > maxDigitsAfterDecimal) return "";
            } else {
                if (newVal.length() > maxDigitsBeforeDecimal) return "";
            }

            return null;
        }
    }
}
