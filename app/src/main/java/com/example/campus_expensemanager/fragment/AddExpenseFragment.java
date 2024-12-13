package com.example.campus_expensemanager.fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    // Khai báo các biến thành viên
    private DatabaseHelper dbHelper;
    private EditText amountEditText, descriptionEditText, dateEditText;
    private Spinner categorySpinner, typeSpinner;
    private String selectedCategory, selectedType, username;
    private Calendar calendar;

    public AddExpenseFragment() {
        // Constructor rỗng
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseHelper(getContext());

        // Khởi tạo các view
        initializeViews(view);

        // Lấy username từ arguments
        if (getArguments() != null) {
            username = getArguments().getString("username");
            loadCategoriesForUser(username);
        }

        // Thiết lập bộ chọn ngày
        setupDatePicker(dateEditText);

        // Thiết lập sự kiện cho nút thêm chi tiêu
        setupAddExpenseButton(view);

        // Thiết lập sự kiện cho nút hiển thị chi tiêu
        setupDisplayButton(view);

        // Thiết lập adapter cho Spinner loại chi tiêu
        setupTypeSpinner();

        return view;
    }

    private void initializeViews(View view) {
        amountEditText = view.findViewById(R.id.amountEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        dateEditText = view.findViewById(R.id.dateEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        typeSpinner = view.findViewById(R.id.typeSpinner);
        calendar = Calendar.getInstance();
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Định dạng ngày đã chọn
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

    private void loadCategoriesForUser(String username) {
        // Lấy danh sách danh mục cho người dùng
        List<String> categories = dbHelper.getCategoriesByUser(username);

        // Tạo adapter cho Spinner danh mục
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Thiết lập sự kiện chọn danh mục
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedCategory = "Other"; // Giá trị mặc định
            }
        });
    }

    private void setupAddExpenseButton(View view) {
        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> addExpense());
    }

    private void setupDisplayButton(View view) {
        Button btnDisplay = view.findViewById(R.id.btnDisplay);
        btnDisplay.setOnClickListener(v -> {
            // Chuyển đến fragment hiển thị chi tiêu
            DisplayExpenseFragment displayExpenseFragment = new DisplayExpenseFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            displayExpenseFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container, displayExpenseFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.expense_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedType = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedType = "Expense"; // Giá trị mặc định
            }
        });
    }

    private void addExpense() {
        // Kiểm tra xem có danh mục nào không
        if (categorySpinner.getAdapter() == null || categorySpinner.getCount() == 0) {
            Toast.makeText(getContext(), "Please add a category before adding expenses.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Lấy thông tin từ các trường nhập
            double amount = Double.parseDouble(amountEditText.getText().toString().trim());
            String description = descriptionEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();

            // Kiểm tra tính hợp lệ của thông tin
            if (amount <= 0 || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm chi tiêu vào cơ sở dữ liệu
            boolean inserted = dbHelper.addExpense(username, amount, description, date, selectedCategory, selectedType);
            if (inserted) {
                // Cập nhật số dư danh mục
                boolean updated = dbHelper.updateCategoryBalance(username, selectedCategory, selectedType, amount);
                if (updated) {
                    Toast.makeText(getContext(), "Expense added and category balance updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error updating category balance", Toast.LENGTH_SHORT).show();
                }

                // Xóa dữ liệu sau khi thêm thành công
                clearInputFields();
            } else {
                Toast.makeText(getContext(), "Error adding expense", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        amountEditText.setText("");
        descriptionEditText.setText("");
        dateEditText.setText("");
        categorySpinner.setSelection(0);
        typeSpinner.setSelection(0);
    }
}