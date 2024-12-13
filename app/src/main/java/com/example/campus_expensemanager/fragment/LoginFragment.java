package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.activity.HomeActivity;
import com.example.campus_expensemanager.database.DatabaseHelper;


public class LoginFragment extends Fragment {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize views
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegister = view.findViewById(R.id.tvRegister);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isValidLogin(username, password)) {
                Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                navigateToHomeFragment(username);
            } else {
                Toast.makeText(getActivity(), "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle register link click
        tvRegister.setOnClickListener(v -> {
            navigateToRegisterFragment();
            Toast.makeText(getActivity(), "Navigate to Register Screen", Toast.LENGTH_SHORT).show();
        });

        // Handle forgot password link click
        tvForgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to Forgot Password Screen
            Toast.makeText(getActivity(), "Navigate to Forgot Password Screen", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
    private void navigateToHomeFragment(String loggedInUsername) {
        Bundle bundle = new Bundle();
        bundle.putString("username", loggedInUsername);

        // Tạo HomeFragment mới và thiết lập Bundle vào fragment
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        // Thực hiện giao diện chuyển fragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment); // R.id.fragment_container là container của Fragment trong activity
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToRegisterFragment() {
        RegisterFragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, registerFragment); // Replace with RegisterFragment
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean isValidLogin(String username, String password) {
        return databaseHelper.checkUserCredentials(username, password);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}

