package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.database.DatabaseHelper;

public class NotificationFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private String username;
    private Button btnMonthlyLimit, btnSpendingChart;

    @Nullable

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Chắc chắn root view đã được gắn vào Activity
        checkExpenseLimit();
    }

    private void addNotification(String startDate, String endDate, double overSpentAmount) {
        Log.d("AddNotification", "Adding notification: OverSpentAmount = " + overSpentAmount);

        // Lấy View root của Fragment
        View rootView = getView();

        if (rootView == null) {
            Log.e("AddNotification", "Root view is null. Cannot add notification.");
            return;
        }

        // Tìm LinearLayout để thêm thông báo
        LinearLayout notificationLayout = rootView.findViewById(R.id.notificationLayout);

        if (notificationLayout == null) {
            Log.e("AddNotification", "Notification layout not found in the current view.");
            return;
        }

        // Tạo LinearLayout mới để chứa icon và văn bản
        LinearLayout notificationContainer = new LinearLayout(requireContext());
        notificationContainer.setOrientation(LinearLayout.HORIZONTAL); // Layout theo chiều ngang
        notificationContainer.setPadding(20, 20, 20, 20); // Thêm padding xung quanh
        notificationContainer.setBackgroundResource(R.drawable.notification_background); // Background góc bo tròn

        // Tạo icon
        ImageView icon = new ImageView(requireContext());
        icon.setImageResource(R.drawable.ic_notification); // Đặt icon từ file drawable
        int iconSize = 70; // Kích thước của icon
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(10, 0, 20, 0); // Thêm margin giữa icon và text
        icon.setLayoutParams(iconParams);

        // Tạo TextView cho thông báo
        TextView notification = new TextView(requireContext());
        notification.setText("Warning: You have exceeded your spending limit by " + overSpentAmount + " from " + startDate + " to " + endDate);
        notification.setTextColor(Color.WHITE); // Màu chữ trắng
        notification.setTextSize(16);
        notification.setShadowLayer(4, 2, 2, Color.BLACK); // Thêm bóng đổ cho văn bản
        notification.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Thêm icon và text vào container
        notificationContainer.addView(icon);
        notificationContainer.addView(notification);

        // Thêm margin cho toàn bộ thông báo
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(10, 10, 10, 10); // Thêm margin xung quanh container
        notificationContainer.setLayoutParams(containerParams);

        // Thêm container vào layout chính
        notificationLayout.addView(notificationContainer);
    }

    private void checkExpenseLimit() {
        // Truy vấn tất cả các giới hạn chi tiêu của người dùng
        Cursor cursor = dbHelper.getSpendingLimitByUsername(username); // Giả sử bạn có phương thức này để lấy tất cả các giới hạn
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") double spendingLimit = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_LIMIT_AMOUNT));
                @SuppressLint("Range") String startDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_START_DATE));
                @SuppressLint("Range") String endDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SPENDING_END_DATE));

                // Tính ngân sách thực tế trong khoảng thời gian
                double netBudget = dbHelper.getNetBudgetInRange(username, startDate, endDate);

                // Ngân sách còn lại = Giới hạn chi tiêu + Ngân sách thực tế
                double remainingBudget = spendingLimit + netBudget;

                // Kiểm tra nếu vượt giới hạn chi tiêu
                if (remainingBudget < 0) {
                    // Thêm thông báo cho giới hạn chi tiêu bị vượt qua
                    addNotification(startDate, endDate, Math.abs(remainingBudget));
                }
            } while (cursor.moveToNext()); // Lặp qua các dòng tiếp theo nếu có
            cursor.close(); // Đóng cursor sau khi hoàn thành
        } else {
            Log.e("CheckExpenseLimit", "No spending limits found for username: " + username);
        }
    }
    private void ViewSpendingLimitScreen() {
        // Giả sử bạn có một AddExpenseFragment hoặc Activity để thêm chi phí
        SpendingLimitFragment spendingLimitFragment = new SpendingLimitFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("username", username); // Truyền username
        spendingLimitFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, spendingLimitFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void ViewSpendingChartScreen() {
        // Giả sử bạn có một AddExpenseFragment hoặc Activity để thêm chi phí
        SpendingChartFragment spendingCharttFragment = new SpendingChartFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("username", username); // Truyền username
        spendingCharttFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, spendingCharttFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
