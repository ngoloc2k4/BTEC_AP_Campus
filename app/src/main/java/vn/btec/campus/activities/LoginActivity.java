package vn.btec.campus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import vn.btec.campus.R;
import vn.btec.campus.utils.SessionManager;
import vn.btec.campus.utils.LanguageUtils;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private CheckBox rememberMeCheckBox;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // If user is already logged in and remember me is enabled, redirect to MainActivity
        if (sessionManager.isLoggedIn() && sessionManager.isRememberMeEnabled()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // Initialize language button click listener
        ImageButton btnLanguage = findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(v -> showLanguageDialog());

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvSignUp);
        rememberMeCheckBox = findViewById(R.id.cbRememberMe);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean rememberMe = rememberMeCheckBox.isChecked();

            // Reset errors
            tilEmail.setError(null);
            tilPassword.setError(null);

            if (TextUtils.isEmpty(email)) {
                tilEmail.setError("Please enter your email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                tilPassword.setError("Please enter your password");
                return;
            }

            if (sessionManager.validateLogin(email, password)) {
                sessionManager.createLoginSession(email, rememberMe);
                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Tiếng Việt"};
        String currentLang = LanguageUtils.getCurrentLanguage(this);
        int selectedIndex = currentLang.equals("en") ? 0 : 1;

        new android.app.AlertDialog.Builder(this)
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
}
