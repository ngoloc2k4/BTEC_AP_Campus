package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.activity.SpendingLimitAdapter;
import com.example.campus_expensemanager.database.DatabaseHelper;
import com.example.campus_expensemanager.entities.SpendingLimit;

import java.util.ArrayList;
import java.util.List;

public class DisplaySpendingLimitFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private SpendingLimitAdapter adapter;
    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_spending_limit, container, false);

        // Initialize database helper
        dbHelper = new DatabaseHelper(getContext());

        // Get username from bundle
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.spending_limit_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load data from database
        List<SpendingLimit> spendingLimitList = loadSpendingLimits();

        // Set up adapter
        adapter = new SpendingLimitAdapter(spendingLimitList);
        recyclerView.setAdapter(adapter);

        // Handle back button

        return view;
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
}
