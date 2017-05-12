package money.mezu.mezu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

public class ExpensesTabFragment extends Fragment implements ExpenseUpdatedListener{

    protected static Budget mCurrentBudget;
    private ExpenseAdapter mExpenseAdapter = null;
    private View mView = null;

    private int mMonth;
    private int mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_expenses, container, false);

        // Create the adapter to convert the array to views
        mExpenseAdapter = new ExpenseAdapter(getActivity(), mCurrentBudget.getExpenses());
        // Attach the adapter to a ListView
        ListView listView = (ListView) mView.findViewById(R.id.expenses_list);
        listView.setAdapter(mExpenseAdapter);

        setupMonthSelection();

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

    private void setupMonthSelection () {
        Calendar calendar = Calendar.getInstance();
        setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

        ImageView nextButton = (ImageView) mView.findViewById(R.id.next_arrow);
        ImageView backButton = (ImageView) mView.findViewById(R.id.back_arrow);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = (mMonth == 12) ? 1 : mMonth + 1;
                int year = (month == 1) ? mYear + 1 : mYear;
                setMonth(month, year);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = (mMonth == 1) ? 12 : mMonth - 1;
                int year = (month == 12) ? mYear - 1 : mYear;
                setMonth(month, year);
            }
        });
    }

    private void setMonth (int month, int year) {
        mMonth = month;
        mYear = year;
        TextView currentMonthTextView = (TextView) mView.findViewById(R.id.current_month);
        currentMonthTextView.setText(mMonth + "/" + mYear);
    }

    public static void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
    }
}
