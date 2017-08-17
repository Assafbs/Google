package money.mezu.mezu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BudgetAdapter extends ArrayAdapter<Budget> {
    public BudgetAdapter(Context context, ArrayList<Budget> budgets) {
        super(context, 0, budgets);
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
        if (budget != null) {
            budgetName.setText(budget.toString());
        }
        budgetRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Access user from within the tag
                Budget budget = (Budget) view.getTag();
                BudgetViewActivity.goToBudgetView(getContext(), budget, ((BaseNavDrawerActivity) getContext()).mSessionManager);
                ((BaseNavDrawerActivity) getContext()).mDrawerLayout.closeDrawers();
            }
        });

        // Return the complete view to render on screen
        return convertView;
    }
}

