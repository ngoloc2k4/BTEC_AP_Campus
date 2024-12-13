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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.activity.SpendingLimitAdapter;
import com.example.campus_expensemanager.database.DatabaseHelper;
import com.example.campus_expensemanager.entities.SpendingLimit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SpendingLimitFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText fromDateEditText, toDateEditText, budgetEditText, notesEditText;
    private Button saveButton,displayButton,updateButton;
    private Calendar calendar;
    private String username;
    private RecyclerView recyclerView;
    private SpendingLimitAdapter adapter;
    public SpendingLimitFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spending_limt, container, false);

        // Initialize views
        dbHelper = new DatabaseHelper(getContext());
        fromDateEditText = view.findViewById(R.id.from_date);
        toDateEditText = view.findViewById(R.id.to_date);
        budgetEditText = view.findViewById(R.id.budget_input);
        notesEditText = view.findViewById(R.id.note_input);
        saveButton = view.findViewById(R.id.save_button);
        displayButton = view.findViewById(R.id.display_spending_limit);
        updateButton = view.findViewById(R.id.btn_update);

        if (getArguments() != null) {
            username = getArguments().getString("username");
        }


        recyclerView = view.findViewById(R.id.spending_limit_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data from database
        List<SpendingLimit> spendingLimitList = loadSpendingLimits();

        // Set up adapter
        adapter = new SpendingLimitAdapter(spendingLimitList);
        recyclerView.setAdapter(adapter);

        // Initialize calendar instance
        calendar = Calendar.getInstance();

        // Set up DatePicker dialogs for both dates
        setupDatePicker(fromDateEditText);
        setupDatePicker(toDateEditText);
        // Handle Save button click
        saveButton.setOnClickListener(v -> saveSpendingLimit());
        updateButton.setOnClickListener(v -> updateSpendingLimit());
//        displayButton.setOnClickListener(v -> deleteSpendingLimit());

        displayButton.setOnClickListener(v -> {

            deleteSpendingLimit();
        });

        return view;
    }

    // Function to set up DatePicker dialog for the EditText
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
    private List<SpendingLimit> loadSpendingLimits() {
        List<SpendingLimit> spendingLimits = new ArrayList<>();
        Cursor cursor = dbHelper.getSpendingLimitByUsername(username);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String amount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_LIMIT_AMOUNT));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_LIMIT_DESCRIPTION));
                @SuppressLint("Range") String startDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_START_DATE));
                @SuppressLint("Range") String endDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_END_DATE));

                String duration = startDate + " - " + endDate;
                spendingLimits.add(new SpendingLimit(amount, description, duration));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return spendingLimits;
    }
    private void deleteSpendingLimit() {
        String description = notesEditText.getText().toString();

        if (description.isEmpty() || username.isEmpty()) {
            Toast.makeText(getActivity(), "Enter the description to delete", Toast.LENGTH_SHORT).show();
        } else {
            // Delete spending limit from the database
            boolean isDeleted = dbHelper.deleteSpendingLimit(description, username);
            if (isDeleted) {
                Toast.makeText(getActivity(), "Spending Limit deleted", Toast.LENGTH_SHORT).show();
                notesEditText.setText("");
                budgetEditText.setText("");
                fromDateEditText.setText("");
                toDateEditText.setText("");
                List<SpendingLimit> updatedSpendingLimits = loadSpendingLimits();
                adapter.updateData(updatedSpendingLimits);


            } else {
                Toast.makeText(getActivity(), "Error deleting spending limit", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void updateSpendingLimit() {
        String description = notesEditText.getText().toString();
        String amount = budgetEditText.getText().toString();
        String startDate = fromDateEditText.getText().toString();
        String endDate = toDateEditText.getText().toString();

        if (description.isEmpty() || amount.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || username.isEmpty()) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update spending limit in the database
        boolean isUpdated = dbHelper.updateSpendingLimit(description, amount, startDate, endDate, username);

        if (isUpdated) {
            Toast.makeText(getActivity(), "Spending Limit updated", Toast.LENGTH_SHORT).show();

            List<SpendingLimit> updatedSpendingLimits = loadSpendingLimits(); // Reload the updated data
            adapter.updateData(updatedSpendingLimits); // Notify adapter of the changes
        } else {
            Toast.makeText(getActivity(), "Error updating spending limit", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to save the entered expense settings
    private void saveSpendingLimit() {
        String description = notesEditText.getText().toString();
        String amount = budgetEditText.getText().toString();
        String startDate = fromDateEditText.getText().toString();
        String endDate = toDateEditText.getText().toString();

        if (description.isEmpty() || amount.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || username.isEmpty()) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            // Insert spending limit into the database
            boolean isInserted = dbHelper.insertSpendingLimit(description, amount, startDate, endDate, username);
            if (isInserted) {
                Toast.makeText(getActivity(), "Spending Limit saved", Toast.LENGTH_SHORT).show();
                notesEditText.setText("");
                budgetEditText.setText("");
                fromDateEditText.setText("");
                toDateEditText.setText("");
                List<SpendingLimit> updatedSpendingLimits = loadSpendingLimits();
                adapter.updateData(updatedSpendingLimits);

            } else {
                Toast.makeText(getActivity(), "Error saving spending limit", Toast.LENGTH_SHORT).show();
            }
        }

    }



}
