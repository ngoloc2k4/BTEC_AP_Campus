package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ChangePasswordFragment extends Fragment {

    // Khai báo các view và biến thành viên
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private String username;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // Khởi tạo các view
        initializeViews(view);

        // Lấy username từ Bundle
        retrieveUsernameFromBundle();

        // Thiết lập sự kiện cho nút thay đổi mật khẩu
        setupChangePasswordButton();

        return view;
    }

    private void initializeViews(View view) {
        etOldPassword = view.findViewById(R.id.et_old_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
    }

    private void retrieveUsernameFromBundle() {
        if (getArguments() != null) {
            username = getArguments().getString("username");
        }
    }

    private void setupChangePasswordButton() {
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        // Lấy thông tin từ các trường nhập
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra tính hợp lệ của các trường nhập
        if (!validateInputs(oldPassword, newPassword, confirmPassword)) return;

        // Kiểm tra mật khẩu cũ trong cơ sở dữ liệu
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Cursor userCursor = dbHelper.getUserByUsername(username);

        // Kiểm tra xem người dùng có tồn tại không
        if (userCursor != null && userCursor.moveToFirst()) {
            @SuppressLint("Range") String storedPassword = userCursor.getString(userCursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));

            // Kiểm tra mật khẩu cũ
            if (!oldPassword.equals(storedPassword)) {
                Toast.makeText(getContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật mật khẩu mới
            if (dbHelper.updatePassword(username, newPassword)) {
                Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                // Quay lại màn hình trước
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String oldPassword, String newPassword, String confirmPassword) {
        // Kiểm tra xem các trường nhập có hợp lệ không
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.equals(oldPassword)) {
            Toast.makeText(getContext(), "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}