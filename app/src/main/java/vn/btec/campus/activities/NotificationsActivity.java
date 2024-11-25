package vn.btec.campus.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import vn.btec.campus.R;
import vn.btec.campus.utils.SessionManager;

public class NotificationsActivity extends AppCompatActivity {

    private SwitchMaterial switchExpenseNotifications;
    private SwitchMaterial switchBudgetAlerts;
    private MaterialButton btnSave;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        sessionManager = new SessionManager(this);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        switchExpenseNotifications = findViewById(R.id.switchExpenseNotifications);
        switchBudgetAlerts = findViewById(R.id.switchBudgetAlerts);
        btnSave = findViewById(R.id.btnSave);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        // Load current settings
        loadCurrentSettings();

        // Set up save button
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void loadCurrentSettings() {
        // Load notification settings
        switchExpenseNotifications.setChecked(sessionManager.getExpenseRemindersEnabled());
        switchBudgetAlerts.setChecked(sessionManager.getBudgetAlertsEnabled());
    }

    private void saveSettings() {
        // Save notification settings
        sessionManager.setExpenseRemindersEnabled(switchExpenseNotifications.isChecked());
        sessionManager.setBudgetAlertsEnabled(switchBudgetAlerts.isChecked());

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
}
