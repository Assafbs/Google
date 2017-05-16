package money.mezu.mezu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by davidled on 21/04/2017.
 */

public class BudgetAdapter extends ArrayAdapter<Budget> {
    //    Context context;
    public BudgetAdapter(Context context, ArrayList<Budget> budgets) {
        super(context, 0, budgets);
//        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Budget budget = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_budget, parent, false);
        }
        // Lookup view for data population
        TextView budgetName = (TextView) convertView.findViewById(R.id.budgetName);
        LinearLayout budgetRow = (LinearLayout) convertView.findViewById(R.id.budgetRow);
        budgetRow.setTag(budget);

        // Populate the data into the template view using the data object
        budgetName.setText(budget.toString());

        budgetRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access user from within the tag
                Budget budget = (Budget) view.getTag();
                BudgetViewActivity.setCurrentBudget(budget);
                Intent budgetViewIntent = new Intent(getContext(), BudgetViewActivity.class);
                getContext().startActivity(budgetViewIntent);
                ((BaseNavDrawerActivity)getContext()).mDrawerLayout.closeDrawers();

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}

