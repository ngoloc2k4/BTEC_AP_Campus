package vn.btec.campus.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import vn.btec.campus.R;
import vn.btec.campus.utils.SessionManager;

public class NotificationSettingsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private SwitchMaterial switchExpenseReminders;
    private SwitchMaterial switchBudgetAlerts;
    private TextInputEditText etNotificationTime;
    private MaterialButton btnSave;
    private Calendar selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        sessionManager = new SessionManager(this);
        initializeViews();
        loadSavedSettings();
        setupListeners();
    }

    private void initializeViews() {
        switchExpenseReminders = findViewById(R.id.switchExpenseReminders);
        switchBudgetAlerts = findViewById(R.id.switchBudgetAlerts);
        etNotificationTime = findViewById(R.id.etNotificationTime);
        btnSave = findViewById(R.id.btnSave);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        selectedTime = Calendar.getInstance();
    }

    private void loadSavedSettings() {
        // Load notification preferences from SessionManager
        switchExpenseReminders.setChecked(sessionManager.getExpenseRemindersEnabled());
        switchBudgetAlerts.setChecked(sessionManager.getBudgetAlertsEnabled());
        
        // Load notification time
        String savedTime = sessionManager.getNotificationTime();
        if (savedTime != null && !savedTime.isEmpty()) {
            etNotificationTime.setText(savedTime);
            String[] timeParts = savedTime.split(":");
            selectedTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            selectedTime.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        }
    }

    private void setupListeners() {
        etNotificationTime.setOnClickListener(v -> showTimePickerDialog());

        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);
                updateTimeDisplay();
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void updateTimeDisplay() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etNotificationTime.setText(timeFormat.format(selectedTime.getTime()));
    }

    private void saveSettings() {
        // Save notification preferences to SessionManager
        sessionManager.setExpenseRemindersEnabled(switchExpenseReminders.isChecked());
        sessionManager.setBudgetAlertsEnabled(switchBudgetAlerts.isChecked());
        sessionManager.setNotificationTime(etNotificationTime.getText().toString());

        // Show success message and finish activity
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
}
