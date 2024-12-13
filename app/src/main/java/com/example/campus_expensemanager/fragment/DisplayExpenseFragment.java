package com.example.campus_expensemanager.fragment;


import static android.app.DownloadManager.COLUMN_DESCRIPTION;
import static android.media.tv.TvContract.WatchNextPrograms.COLUMN_TYPE;

import static com.example.campus_expensemanager.database.DatabaseHelper.COLUMN_AMOUNT;
import static com.example.campus_expensemanager.database.DatabaseHelper.COLUMN_CATEGORY;
import static com.example.campus_expensemanager.database.DatabaseHelper.COLUMN_DATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.activity.ExpenseAdapter;
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

public class DisplayExpenseFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText fromDate, toDate;
    private Button filterExpense;
    private ExpenseAdapter adapter;
    private Calendar calendar;
    private String username;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_expense, container, false);

        fromDate = view.findViewById(R.id.from_date);
        toDate = view.findViewById(R.id.to_date);
        filterExpense = view.findViewById(R.id.filter_button);
        calendar = Calendar.getInstance();
        setupDatePicker(fromDate);
        setupDatePicker(toDate);

        // Nhận username từ Bundle nếu có
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rv_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Hiển thị chi tiêu của người dùng
        displayUserExpenses(username);

        filterExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayUserExpenses(username);
            }
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

    // Hàm để hiển thị chi tiêu của người dùng từ cơ sở dữ liệu
    private void displayUserExpenses(String username) {
        if (username == null || username.isEmpty()) {
            Toast.makeText(getContext(), "Username is null or empty", Toast.LENGTH_SHORT).show();
            return; // Dừng phương thức nếu username không hợp lệ
        }

        // Lấy ngày từ EditText
        String fromDateString = fromDate.getText().toString();
        String toDateString = toDate.getText().toString();

        // Chuyển đổi ngày từ chuỗi sang Date nếu có
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date fromDate = null;
        Date toDate = null;

        try {
            if (!fromDateString.isEmpty()) {
                fromDate = sdf.parse(fromDateString);
            }
            if (!toDateString.isEmpty()) {
                toDate = sdf.parse(toDateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Khởi tạo DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Cursor cursor = dbHelper.getExpensesByUsername(username);

        if (cursor != null && cursor.moveToFirst()) {
            List<Expense> expenses = new ArrayList<>();
            do {
                // Lấy dữ liệu từ cursor
                @SuppressLint("Range") int amount = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TYPE));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY));

                // Chuyển đổi ngày từ chuỗi sang Date
                Date expenseDate = null;
                try {
                    expenseDate = sdf.parse(date);  // giả sử ngày trong DB là chuỗi định dạng "dd/MM/yyyy"
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Nếu ngày chi tiêu nằm trong khoảng thời gian đã chọn
                if ((fromDate == null || expenseDate.after(fromDate) || expenseDate.equals(fromDate)) &&
                        (toDate == null || expenseDate.before(toDate) || expenseDate.equals(toDate))) {
                    expenses.add(new Expense(amount, description, date, type, category));
                }
            } while (cursor.moveToNext());

            // Thiết lập adapter cho RecyclerView
            adapter = new ExpenseAdapter(getContext(), expenses);
            recyclerView.setAdapter(adapter);
        }
    }

}
