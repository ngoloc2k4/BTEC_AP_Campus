package vn.btec.campus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import vn.btec.campus.R;
import vn.btec.campus.activities.BudgetSettingsActivity;
import vn.btec.campus.data.DatabaseHelper;
import vn.btec.campus.utils.SessionManager;

public class DashboardFragment extends Fragment {
    private TextView tvBudgetLimit;
    private TextView tvCurrentSpending;
    private TextView tvRemainingBudget;
    private LinearProgressIndicator progressBudget;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize components
        tvBudgetLimit = view.findViewById(R.id.tvBudgetLimit);
        tvCurrentSpending = view.findViewById(R.id.tvCurrentSpending);
        tvRemainingBudget = view.findViewById(R.id.tvRemainingBudget);
        progressBudget = view.findViewById(R.id.progressBudget);
        CardView cardBudget = view.findViewById(R.id.cardBudget);

        // Initialize helpers
        databaseHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Setup click listeners
        cardBudget.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BudgetSettingsActivity.class);
            startActivity(intent);
        });

        // Load and display data
        updateDashboard();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void updateDashboard() {
        // Get budget limit from session
        double budgetLimit = sessionManager.getBudgetLimit();
        
        // Get current month's spending from database
        double currentSpending = databaseHelper.getCurrentMonthSpending();
        
        // Calculate remaining budget
        double remainingBudget = budgetLimit - currentSpending;

        // Update UI
        tvBudgetLimit.setText(String.format("Budget: $%.2f", budgetLimit));
        tvCurrentSpending.setText(String.format("Spent: $%.2f", currentSpending));
        tvRemainingBudget.setText(String.format("Remaining: $%.2f", remainingBudget));

        // Update progress bar
        if (budgetLimit > 0) {
            int progress = (int) ((currentSpending / budgetLimit) * 100);
            progressBudget.setProgress(Math.min(progress, 100));
        } else {
            progressBudget.setProgress(0);
        }
    }
}
