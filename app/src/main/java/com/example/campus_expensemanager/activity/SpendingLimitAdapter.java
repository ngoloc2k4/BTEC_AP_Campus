package com.example.campus_expensemanager.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_expensemanager.R;
import com.example.campus_expensemanager.entities.SpendingLimit;

import java.util.List;

public class SpendingLimitAdapter extends RecyclerView.Adapter<SpendingLimitAdapter.ViewHolder> {


    private final List<SpendingLimit> spendingLimitList;

    public SpendingLimitAdapter(List<SpendingLimit> spendingLimitList) {
        this.spendingLimitList = spendingLimitList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_spending_limit, parent, false);
        return new ViewHolder(view);
    }
    public void updateData(List<SpendingLimit> newSpendingLimitList) {
        this.spendingLimitList.clear();
        this.spendingLimitList.addAll(newSpendingLimitList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpendingLimit spendingLimit = spendingLimitList.get(position);

        // Bind data to views
        holder.amountText.setText("Amount: " + spendingLimit.getAmount());
        holder.descriptionText.setText("Description: " + spendingLimit.getDescription());
        holder.durationText.setText("Duration: " + spendingLimit.getDuration());
        holder.icon.setImageResource(R.drawable.spending); // Replace with your actual drawable
    }

    @Override
    public int getItemCount() {
        return spendingLimitList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView amountText;
        private final TextView descriptionText;
        private final TextView durationText;
        private final ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.item_amount_display);
            descriptionText = itemView.findViewById(R.id.item_description);
            durationText = itemView.findViewById(R.id.item_duration);
            icon = itemView.findViewById(R.id.ic_spending_limit);
        }
    }
}
