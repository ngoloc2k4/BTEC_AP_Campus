package com.example.campus_expensemanager.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.database.DatabaseHelper;
import com.example.campus_expensemanager.entities.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // Khai báo biến để lưu trữ ngữ cảnh và danh sách chi tiêu
    private Context context;
    private List<Expense> expenses;
    private List<Expense> filteredList;

    // Constructor để khởi tạo adapter
    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
        this.filteredList = new ArrayList<>(expenses); // Khởi tạo danh sách lọc
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo và trả về ViewHolder cho mỗi item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        // Liên kết dữ liệu chi tiêu với ViewHolder
        Expense expense = expenses.get(position);
        int amount = expense.getAmount();

        // Đổi màu số tiền dựa trên loại chi tiêu
        if (expense.getType().equals("Income")) {
            // Nếu là thu nhập, màu xanh lá cây
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            // Nếu là chi tiêu, màu đỏ
            holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }

        // Thiết lập văn bản cho các trường thông tin chi tiêu
        holder.amount.setText(String.format("Amount : %,d", amount));
        holder.description.setText("Description: " + expense.getDescription());
        holder.date.setText("Date: " + expense.getDate());
        holder.type.setText("Type: " + expense.getType());
        holder.category.setText("Category: " + expense.getCategory());

        /////cap nhat su kien cho nut xoa




    }
    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách đã lọc
        return filteredList.size();
    }

    // ViewHolder cho từng item trong RecyclerView
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView amount, description, date, type, category;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khởi tạo các TextView từ layout
            amount = itemView.findViewById(R.id.tv_amount);
            description = itemView.findViewById(R.id.tv_description);
            date = itemView.findViewById(R.id.tv_date);
            type = itemView.findViewById(R.id.tv_type);
            category = itemView.findViewById(R.id.tv_category);
        }
    }

    // Phương thức cập nhật dữ liệu của adapter
    public void updateData(List<Expense> newExpenses) {
        expenses.clear(); // Xóa danh sách cũ
        expenses.addAll(newExpenses); // Thêm danh sách mới
        filteredList.clear(); // Xóa danh sách đã lọc
        filteredList.addAll(newExpenses); // Cập nhật danh sách đã lọc
        notifyDataSetChanged(); // Thông báo RecyclerView cập nhật
    }

    // Phương thức lọc danh sách chi tiêu theo từ khóa
    public void filter(String keyword) {
        filteredList.clear(); // Xóa danh sách đã lọc
        if (keyword.isEmpty()) {
            // Nếu từ khóa trống, hiển thị toàn bộ danh sách
            filteredList.addAll(expenses);
        } else {
            // Lọc danh sách dựa trên từ khóa
            for (Expense expense : expenses) {
                if (expense.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        expense.getType().toLowerCase().contains(keyword.toLowerCase()) ||
                        expense.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(expense);
                }
            }
        }
        notifyDataSetChanged(); // Cập nhật RecyclerView
    }

}