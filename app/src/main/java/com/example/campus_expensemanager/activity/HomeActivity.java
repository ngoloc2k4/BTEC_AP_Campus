package com.example.campus_expensemanager.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.fragment.HomeFragment;

public class HomeActivity extends AppCompatActivity {

    // Khai báo biến để quản lý cơ sở dữ liệu và fragment
    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập giao diện cho activity
        setContentView(R.layout.activity_home);

        // Tải HomeFragment khi activity được khởi tạo
        loadFragment(new HomeFragment());
    }

    // Phương thức để thay thế fragment hiện tại
    private void loadFragment(Fragment fragment) {
        // Bắt đầu giao dịch fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Thay thế fragment trong container
        transaction.replace(R.id.fragment_container, fragment);
        // Thêm giao dịch vào back stack để có thể quay lại
        transaction.addToBackStack(null);
        // Cam kết giao dịch
        transaction.commit();
    }
}