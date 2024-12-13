package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.View.CustomBarChartView;
import com.example.campus_expensemanager.database.DatabaseHelper;
import com.example.campus_expensemanager.entities.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SpendingChartFragment extends Fragment {

    private String username;
    private DatabaseHelper dbHelper;
    private Button generateChart;
    private EditText fromDate, toDate;
    private CustomBarChartView chartView;
    private Calendar calendar;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        dbHelper = new DatabaseHelper(getContext());
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
        fromDate = view.findViewById(R.id.from_date);
        toDate = view.findViewById(R.id.to_date);
        generateChart = view.findViewById(R.id.generate_chart);
        calendar = Calendar.getInstance();
        chartView = view.findViewById(R.id.chart_view);
        setupDatePicker(fromDate);
        setupDatePicker(toDate);

        generateChart.setOnClickListener(v -> {
            String fromDateStr = fromDate.getText().toString();
            String toDateStr = toDate.getText().toString();

            // Lấy dữ liệu chi tiêu từ database
            List<Expense> expenses = getExpensesFromDatabase(username, fromDateStr, toDateStr);

            // Cập nhật biểu đồ với dữ liệu chi tiêu

            chartView.setExpenses(expenses);
        });
        return view;
    }
    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Format the selected date
                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                        editText.setText(formattedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }
    private List<Expense> getExpensesFromDatabase(String username, String fromDateStr, String toDateStr) {
        List<Expense> expenses = new ArrayList<>();

        // Truy vấn dữ liệu chi tiêu theo username
        Cursor cursor = dbHelper.getExpensesByUsername(username);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int amount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY));

                // Kiểm tra nếu chi tiêu nằm trong khoảng thời gian đã chọn
                if (isWithinDateRange(date, fromDateStr, toDateStr)) {
                    expenses.add(new Expense(amount, date,type,category, description));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return expenses;
    }

    // Kiểm tra xem ngày chi tiêu có nằm trong khoảng từDateStr đến toDateStr không
    private boolean isWithinDateRange(String expenseDate, String fromDateStr, String toDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date expense = sdf.parse(expenseDate);
            Date from = sdf.parse(fromDateStr);
            Date to = sdf.parse(toDateStr);

            // Nếu ngày chi tiêu nằm trong khoảng, trả về true
            return expense != null && expense.after(from) && expense.before(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
