package money.mezu.mezu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExpensesTabFragment extends Fragment implements ExpenseUpdatedListener{

    protected static Budget mCurrentBudget;
    private ExpenseAdapter mExpenseAdapter = null;
    private View mView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_expenses, container, false);

        // Create the adapter to convert the array to views
        mExpenseAdapter = new ExpenseAdapter(getActivity(), mCurrentBudget.getExpenses());
        // Attach the adapter to a ListView
        ListView listView = (ListView) mView.findViewById(R.id.expenses_list);
        listView.setAdapter(mExpenseAdapter);

        return mView;
    }

    public void expenseUpdatedCallback()
    {
        Log.d("","BudgetViewActivity:expenseUpdatedCallback: invoked");
        for(Expense expense: mCurrentBudget.getExpenses())
        {
            Log.d("",String.format("BudgetViewActivity:expenseUpdatedCallback: has expense: %s", expense.getTitle()));
        }

        mExpenseAdapter = new ExpenseAdapter(getActivity(), mCurrentBudget.getExpenses());
        // Attach the adapter to a ListView
        ListView listView = (ListView) mView.findViewById(R.id.expenses_list);
        listView.setAdapter(mExpenseAdapter);
        listView.invalidate();
    }

    public static void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
    }
}
