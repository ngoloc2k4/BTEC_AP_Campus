package com.example.campus_expensemanager.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddCategoryFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private EditText etCategoryName, etCategoryDescription, etCategoryDate;
    private Button btnAdd;
    private String username;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseHelper(getContext());

        // Khởi tạo các view
        initializeViews(view);

        // Lấy tên người dùng từ arguments
        retrieveUsernameFromArguments();

        // Thiết lập bộ chọn ngày cho trường nhập ngày
        setupDatePicker(etCategoryDate);

        // Thiết lập sự kiện click cho nút thêm
        setupAddButtonClickListener();

        return view;
    }

    private void initializeViews(View view) {
        etCategoryName = view.findViewById(R.id.et_category_name);
        etCategoryDescription = view.findViewById(R.id.et_category_description);
        etCategoryDate = view.findViewById(R.id.et_category_date);
        btnAdd = view.findViewById(R.id.btn_add);
        calendar = Calendar.getInstance();
    }

    private void retrieveUsernameFromArguments() {
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
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

    private void setupAddButtonClickListener() {
        btnAdd.setOnClickListener(v -> addCategory());
    }

    private void addCategory() {
        // Lấy thông tin từ các trường nhập
        String name = etCategoryName.getText().toString().trim();
        String description = etCategoryDescription.getText().toString().trim();
        String date = etCategoryDate.getText().toString().trim(); // Trường ngày tùy chọn

        // Kiểm tra tính hợp lệ của tên danh mục và mô tả
        if (!validateInputs(name, description, date)) return;

        // Kiểm tra xem danh mục đã tồn tại chưa
        if (dbHelper.isCategoryExists(username, name)) {
            showToast("Category already exists. Please choose a different name.");
            clearInputFields();
            return;
        }

        // Thêm danh mục vào cơ sở dữ liệu
        if (dbHelper.addCategory(username, name, description, date)) {
            showToast("Category added successfully");
            clearInputFields();
            // Quay lại fragment trước đó
            getParentFragmentManager().popBackStack();
        } else {
            showToast("Failed to add category");
        }
    }

    private boolean validateInputs(String name, String description, String date) {
        if (name.isEmpty()) {
            showToast("Category name cannot be empty");
            return false;
        }
        if (description.isEmpty()) {
            showToast("Category description cannot be empty");
            return false;
        }
        if (date.isEmpty()) {
            showToast("Please enter a date for the category");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearInputFields() {
        etCategoryName.setText("");
        etCategoryDescription.setText("");
        etCategoryDate.setText(""); // Xóa trường ngày nếu đã thêm
    }
}