package com.example.campus_expensemanager.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.campus_expensemanager.entities.Expense;

import java.util.List;

public class CustomBarChartView extends View {

    private List<Expense> expenses;  // Dữ liệu chi tiêu (expenses)
    private Paint barPaint;
    private Paint textPaint;

    public CustomBarChartView(Context context) {
        super(context);
        init();
    }

    public CustomBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setColor(Color.GREEN);  // Chọn màu cho các thanh trong biểu đồ
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    // Phương thức để set dữ liệu chi tiêu
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        invalidate(); // Gọi lại onDraw() để vẽ lại biểu đồ
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (expenses == null || expenses.size() == 0) return;

        // Vẽ các thanh biểu đồ cho mỗi chi tiêu
        int barWidth = getWidth() / expenses.size(); // Tính chiều rộng của mỗi thanh
        int maxAmount = getMaxExpenseAmount();

        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            float barHeight = (expense.getAmount() / (float) maxAmount) * getHeight();
            float left = i * barWidth;
            float top = getHeight() - barHeight;
            float right = left + barWidth;
            float bottom = getHeight();

            // Vẽ thanh biểu đồ
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Vẽ số tiền trên thanh biểu đồ
            canvas.drawText(String.valueOf(expense.getAmount()), left + barWidth / 2, top - 10, textPaint);
        }
    }

    private int getMaxExpenseAmount() {
        int max = 0;
        for (Expense expense : expenses) {
            if (expense.getAmount() > max) {
                max = expense.getAmount();
            }
        }
        return max;
    }
}
