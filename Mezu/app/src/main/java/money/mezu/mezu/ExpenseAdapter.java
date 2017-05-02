package money.mezu.mezu;

import android.widget.ArrayAdapter;

/**
 * Created by davidled on 22/04/2017.
 */
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import java.util.ArrayList;

public class ExpenseAdapter extends ArrayAdapter<Expense> {
    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        super(context, 0, expenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        //BAHH
        if (expense.getCategory() != null) {
            category.setText(expense.getCategory().toString());
        } else {
            category.setText(R.string.category_other);
        }
        amount.setText(Double.toString(expense.getAmount()));
        String t_title = expense.getTitle();
        if (t_title == null) {
            title.setText(R.string.general);
        } else {
            title.setText(expense.getTitle());
        }

        expenseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access user from within the tag
                Expense expense = (Expense) view.getTag();
//                BudgetViewActivity.setCurrentBudget(budget);
//                Intent budgetViewIntent = new Intent(getContext(), BudgetViewActivity.class);
//                getContext().startActivity(budgetViewIntent);

                // TODO: popup with expense info?
                Toast.makeText(getContext(), "TODO: pop-up expense info?", Toast.LENGTH_SHORT).show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
