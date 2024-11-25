package vn.btec.campus.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.btec.campus.R;
import vn.btec.campus.data.DatabaseHelper;
import vn.btec.campus.models.RecurringExpense;

public class StatisticsFragment extends Fragment {
    private PieChart pieChart;
    private TextView tvTotalSpending;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        pieChart = view.findViewById(R.id.pieChart);
        tvTotalSpending = view.findViewById(R.id.tvTotalSpending);
        databaseHelper = new DatabaseHelper(requireContext());

        // Setup pie chart
        setupPieChart();
        
        // Load and display data
        updateStatistics();
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Expenses by Category");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);
    }

    private void updateStatistics() {
        // Get all recurring expenses
        List<RecurringExpense> expenses = databaseHelper.getAllRecurringExpenses();
        
        // Calculate total spending and category distribution
        double totalSpending = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (RecurringExpense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            totalSpending += amount;

            // Add to category totals
            categoryTotals.put(category, 
                categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        // Update total spending display
        tvTotalSpending.setText(String.format("Total Monthly Spending: $%.2f", totalSpending));

        // Create pie chart entries
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        // Create and style the pie chart dataset
        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Create and set the pie data
        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        // Update the chart
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatistics();
    }
}
