package vn.btec.campus.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import vn.btec.campus.R;
import vn.btec.campus.models.RecurringExpense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseDialog extends DialogFragment {
    private OnExpenseAddedListener listener;
    private TextInputEditText etName;
    private TextInputEditText etAmount;
    private Spinner spinnerFrequency;
    private TextInputEditText etDueDate;
    private TextInputEditText etReminderDays;
    private Spinner spinnerCategory;
    private Date selectedDate;

    public interface OnExpenseAddedListener {
        void onExpenseAdded(RecurringExpense expense);
    }

    public void setOnExpenseAddedListener(OnExpenseAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_recurring_expense, null);

        // Initialize views
        etName = view.findViewById(R.id.etName);
        etAmount = view.findViewById(R.id.etAmount);
        spinnerFrequency = view.findViewById(R.id.spinnerFrequency);
        etDueDate = view.findViewById(R.id.etDueDate);
        etReminderDays = view.findViewById(R.id.etReminderDays);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        // Set up frequency spinner
        ArrayAdapter<CharSequence> frequencyAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.frequency_options, android.R.layout.simple_spinner_item);
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrequency.setAdapter(frequencyAdapter);

        // Set up category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.category_options, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Set up date picker
        etDueDate.setOnClickListener(v -> showDatePicker());

        builder.setView(view)
                .setTitle("Add Recurring Expense")
                .setPositiveButton("Add", (dialog, which) -> {
                    if (validateInput()) {
                        RecurringExpense expense = createExpenseFromInput();
                        if (listener != null) {
                            listener.onExpenseAdded(expense);
                        }
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select due date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            etDueDate.setText(dateFormat.format(selectedDate));
        });

        datePicker.show(getChildFragmentManager(), "DUE_DATE_PICKER");
    }

    private boolean validateInput() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (etAmount.getText().toString().trim().isEmpty()) {
            etAmount.setError("Amount is required");
            return false;
        }
        if (selectedDate == null) {
            etDueDate.setError("Due date is required");
            return false;
        }
        if (etReminderDays.getText().toString().trim().isEmpty()) {
            etReminderDays.setError("Reminder days is required");
            return false;
        }
        return true;
    }

    private RecurringExpense createExpenseFromInput() {
        String name = etName.getText().toString().trim();
        double amount = Double.parseDouble(etAmount.getText().toString().trim());
        String frequency = spinnerFrequency.getSelectedItem().toString();
        int reminderDays = Integer.parseInt(etReminderDays.getText().toString().trim());
        String category = spinnerCategory.getSelectedItem().toString();

        return new RecurringExpense(name, amount, frequency, selectedDate, reminderDays, category);
    }
}
