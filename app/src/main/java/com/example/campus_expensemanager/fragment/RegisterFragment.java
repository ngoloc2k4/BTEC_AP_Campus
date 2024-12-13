package com.example.campus_expensemanager.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
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

public class RegisterFragment extends Fragment {

    private EditText etFullName, etEmail, etUsername, etPassword, etPhone;
    private Button btnRegister;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Initialize views
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etPhone = view.findViewById(R.id.etPhone);
        btnRegister = view.findViewById(R.id.btnRegister);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Set onClick listener for the register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                // Validate input
                if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the email or username already exists
                if (isUserExists(email, username)) {
                    Toast.makeText(getActivity(), "Email or Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert user into the database
                boolean isInserted = databaseHelper.insertUser(fullName, email, username, password, phone);

                if (isInserted) {
                    Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Navigate back
                } else {
                    Toast.makeText(getActivity(), "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean isUserExists(String email, String username) {
        Cursor cursor = databaseHelper.getAllUsers();
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String existingEmail = cursor.getString(cursor.getColumnIndex("email"));
            @SuppressLint("Range") String existingUsername = cursor.getString(cursor.getColumnIndex("username"));
            if (existingEmail.equals(email) || existingUsername.equals(username)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
