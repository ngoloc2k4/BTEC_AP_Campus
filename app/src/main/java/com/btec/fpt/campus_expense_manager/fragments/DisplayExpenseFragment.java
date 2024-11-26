package com.btec.fpt.campus_expense_manager.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.btec.fpt.campus_expense_manager.R;
import com.btec.fpt.campus_expense_manager.database.DatabaseHelper;
import com.btec.fpt.campus_expense_manager.entities.Transaction;

import java.util.ArrayList;

public class DisplayExpenseFragment extends Fragment {
    public DisplayExpenseFragment(){
    }

    private DatabaseHelper dbHelper;
    private ListView expensesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display_expense, container, false);
        dbHelper = new DatabaseHelper(getContext());
        expensesListView = view.findViewById(R.id.expensesListView);
        loadExpenses();
        return view;
    }

    private void loadExpenses() {
        ArrayList<String> expenseList = new ArrayList<>();

//        for(Transaction transaction: dbHelper.getTransactionList() )
//        {
//
//
//            expenseList.add(date + " - " + description + ": $" + amount);
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, expenseList);
//        expensesListView.setAdapter(adapter);
    }

}
