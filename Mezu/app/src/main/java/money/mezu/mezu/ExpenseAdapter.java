package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;



public class ExpenseAdapter extends ArrayAdapter<Expense> {
    Context mContext;
    BudgetViewActivity mActivity;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        super(context, 0, expenses);
        mContext = context;
        mActivity = (BudgetViewActivity) context;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Expense expense = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_expense, parent, false);
        }
        // Lookup view for data population
        TextView category = (TextView) convertView.findViewById(R.id.expenseCategory);
        TextView amount = (TextView) convertView.findViewById(R.id.expenseAmount);
        TextView title = (TextView) convertView.findViewById(R.id.expenseTitle);
        LinearLayout expenseRow = (LinearLayout) convertView.findViewById(R.id.expenseRow);
        expenseRow.setTag(expense);

        // Populate the data into the template view using the data object
        if (expense.getCategory() != null) {
            category.setText(expense.getCategory().toString());
        } else {
            category.setText(R.string.category_other);
        }
        amount.setText(Double.toString(expense.getAmount()));
        if (!expense.getIsExpense()) {
            amount.setText(Double.toString(expense.getAmount()) + "+");
            amount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        String t_title = expense.getTitle();
        if (t_title == null) {
            title.setText(R.string.general);
        } else {
            title.setText(expense.getTitle());
        }

        expenseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense expense = (Expense) view.getTag();
                showExpense(expense);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void showExpense (Expense expense) {
        ExpenseFragment expenseFragment = new ExpenseFragment();
        expenseFragment.setShowExpense(expense);
        mActivity.setExpenseFragment(expenseFragment);
    }
}
