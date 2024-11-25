package vn.btec.campus.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import vn.btec.campus.R;
import vn.btec.campus.models.RecurringExpense;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecurringExpenseAdapter extends RecyclerView.Adapter<RecurringExpenseAdapter.ExpenseViewHolder> {
    private final List<RecurringExpense> expenses;
    private final SimpleDateFormat dateFormat;

    public RecurringExpenseAdapter(List<RecurringExpense> expenses) {
        this.expenses = expenses;
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recurring_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        RecurringExpense expense = expenses.get(position);
        
        holder.tvExpenseName.setText(expense.getName());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "$%.2f", expense.getAmount()));
        holder.tvDueDate.setText(String.format("Due on %s", dateFormat.format(expense.getDueDate())));
        holder.chipReminder.setText(String.format("Reminder: %d days before", expense.getReminderDays()));

        // Set icon based on category (you can add more category icons)
        switch (expense.getCategory().toLowerCase()) {
            case "rent":
                holder.ivIcon.setImageResource(R.drawable.ic_home);
                break;
            case "subscription":
                holder.ivIcon.setImageResource(R.drawable.ic_subscription);
                break;
            default:
                holder.ivIcon.setImageResource(R.drawable.ic_expense);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvExpenseName;
        TextView tvAmount;
        TextView tvDueDate;
        Chip chipReminder;

        ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvExpenseName = itemView.findViewById(R.id.tvExpenseName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            chipReminder = itemView.findViewById(R.id.chipReminder);
        }
    }
}
