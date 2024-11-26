package vn.btec.campus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import vn.btec.campus.R;
import vn.btec.campus.activities.LoginActivity;
import vn.btec.campus.activities.BudgetSettingsActivity;
import vn.btec.campus.activities.NotificationsActivity;
import vn.btec.campus.utils.SessionManager;
import vn.btec.campus.utils.LanguageUtils;

public class ProfileFragment extends Fragment {

    private ShapeableImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvEmail;
    private View layoutBudgetSettings;
    private View layoutNotifications;
    private View layoutAbout;
    private View layoutLanguage;
    private MaterialButton btnSignOut;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        // Initialize views
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        layoutBudgetSettings = view.findViewById(R.id.budgetSettingsLayout);
        layoutNotifications = view.findViewById(R.id.notificationsLayout);
        layoutLanguage = view.findViewById(R.id.languageSettingsLayout);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        // Set actual user data
        tvUsername.setText(sessionManager.getUsername());
        tvEmail.setText(sessionManager.getEmail());

        // Set click listeners
        layoutBudgetSettings.setOnClickListener(v -> {
            // Navigate to budget settings
            Intent intent = new Intent(requireContext(), BudgetSettingsActivity.class);
            startActivity(intent);
        });

        layoutNotifications.setOnClickListener(v -> {
            // Navigate to notifications settings
            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
            startActivity(intent);
        });

        layoutLanguage.setOnClickListener(v -> {
            showLanguageDialog();
        });

        btnSignOut.setOnClickListener(v -> {
            // Show confirmation dialog
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        ivProfilePic.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Profile picture update will be available soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Tiếng Việt"};
        String currentLang = LanguageUtils.getCurrentLanguage(requireContext());

        int selectedIndex = currentLang.equals("en") ? 0 : 1;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.language)
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String selectedLang = which == 0 ? "en" : "vi";
                    if (!selectedLang.equals(currentLang)) {
                        LanguageUtils.setLocale(requireActivity(), selectedLang);
                        requireActivity().recreate();
                    }
                    dialog.dismiss();
                })
                .show();
    }



}
