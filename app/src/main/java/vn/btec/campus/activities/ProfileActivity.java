package vn.btec.campus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import vn.btec.campus.R;

public class ProfileActivity extends AppCompatActivity {
    private LinearLayout budgetSettingsLayout;
    private LinearLayout notificationsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
        }

        // Initialize views
        budgetSettingsLayout = findViewById(R.id.budgetSettingsLayout);
        notificationsLayout = findViewById(R.id.notificationsLayout);

        // Set click listener for budget settings
        budgetSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, BudgetSettingsActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for notifications
        notificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });
    }
}
