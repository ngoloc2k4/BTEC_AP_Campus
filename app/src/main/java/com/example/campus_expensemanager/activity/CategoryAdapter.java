package com.example.campus_expensemanager.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // Khai báo biến để lưu trữ ngữ cảnh và danh sách danh mục
    private Context context;
    private List<Category> categoryList;
    private List<Category> filteredList;

    // Constructor để khởi tạo adapter
    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.filteredList = new ArrayList<>(categoryList); // Khởi tạo danh sách lọc
    }

    // Phương thức để thiết lập danh sách danh mục mới
    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
        notifyDataSetChanged(); // Thông báo adapter cập nhật RecyclerView
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo và trả về ViewHolder cho mỗi item
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Lấy danh mục tại vị trí hiện tại
        Category category = categoryList.get(position);



        // Thiết lập tên, số tiền và ngày cho các TextView tương ứng
        holder.itemName.setText(category.getName()); // Tên danh mục
        holder.itemAmount.setText("Amount: " + category.getAmount()); // Số tiền
        holder.itemDate.setText(category.getDateCreated()); // Ngày tạo

        // Thiết lập hình ảnh dựa trên số tiền
        if (category.getAmount() < 0) {
            // Nếu số tiền âm, hiển thị hình ảnh chi tiêu
            holder.itemImage.setImageResource(R.drawable.ic_expense);
        } else {
            // Nếu số tiền dương, hiển thị hình ảnh thu nhập
            holder.itemImage.setImageResource(R.drawable.item7);
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng danh mục
        return categoryList.size();
    }

    // ViewHolder cho từng item trong RecyclerView
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các view
        TextView itemName, itemAmount, itemDate;
        ImageView itemImage; // Hình ảnh biểu tượng cho danh mục

        public CategoryViewHolder(View itemView) {
            super(itemView);
            // Khởi tạo các view từ layout
            itemName = itemView.findViewById(R.id.item_Title);
            itemAmount = itemView.findViewById(R.id.item_amount_display);
            itemDate = itemView.findViewById(R.id.item_time);
            itemImage = itemView.findViewById(R.id.ic_expense);
        }
    }
}