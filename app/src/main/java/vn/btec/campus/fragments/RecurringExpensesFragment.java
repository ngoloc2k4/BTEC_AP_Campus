package vn.btec.campus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import vn.btec.campus.R;
import vn.btec.campus.adapters.RecurringExpenseAdapter;
import vn.btec.campus.data.DatabaseHelper;
import vn.btec.campus.models.RecurringExpense;
import vn.btec.campus.dialogs.AddExpenseDialog;

import java.util.ArrayList;
import java.util.List;

public class RecurringExpensesFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecurringExpenseAdapter adapter;
    private TextView tvMonthlyTotal;
    private DatabaseHelper databaseHelper;
    private List<RecurringExpense> expenseList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recurring_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.rvExpenses);
        tvMonthlyTotal = view.findViewById(R.id.tvMonthlyTotal);
        FloatingActionButton fabAddExpense = view.findViewById(R.id.fabAddExpense);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(requireContext());

        // Initialize RecyclerView
        expenseList = new ArrayList<>();
        adapter = new RecurringExpenseAdapter(expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Load expenses
        loadExpenses();

        // Set FAB click listener
        fabAddExpense.setOnClickListener(v -> showAddExpenseDialog());
    }

    private void loadExpenses() {
        expenseList.clear();
        expenseList.addAll(databaseHelper.getAllRecurringExpenses());
        adapter.notifyDataSetChanged();
        updateMonthlyTotal();
    }

    private void updateMonthlyTotal() {
        double total = 0;
        for (RecurringExpense expense : expenseList) {
            total += expense.getAmount();
        }
        tvMonthlyTotal.setText(String.format("$%.2f", total));
    }

    private void showAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog();
        dialog.show(getChildFragmentManager(), "AddExpenseDialog");
        dialog.setOnExpenseAddedListener(expense -> {
            // Add expense to database
            long id = databaseHelper.addRecurringExpense(expense);
            if (id != -1) {
                expense.setId(id);
                expenseList.add(expense);
                adapter.notifyItemInserted(expenseList.size() - 1);
                updateMonthlyTotal();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }
}
