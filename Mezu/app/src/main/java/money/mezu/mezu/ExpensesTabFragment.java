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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ExpensesTabFragment extends Fragment implements ExpenseUpdatedListener{

    protected Budget mCurrentBudget;
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
        EventDispatcher.getInstance().registerExpenseUpdateListener(this);
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
        filterExpenses(mMonth, mYear);
    }

    private void setupMonthSelection () {
        Calendar calendar = Calendar.getInstance();
        setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

        ImageView nextButton = (ImageView) mView.findViewById(R.id.next_arrow);
        ImageView backButton = (ImageView) mView.findViewById(R.id.back_arrow);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = nextMonth(mMonth);
                int year = (month == 1) ? mYear + 1 : mYear;
                setMonth(month, year);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = previousMonth(mMonth);
                int year = (month == 12) ? mYear - 1 : mYear;
                setMonth(month, year);
            }
        });
    }

    private void setMonth(int month, int year) {
        mMonth = month;
        mYear = year;
        TextView currentMonthTextView = (TextView) mView.findViewById(R.id.current_month);
        currentMonthTextView.setText(mMonth + "/" + mYear);
        filterExpenses(mMonth, mYear);
    }

    private void filterExpenses(int month, int year) {
        ListView listView = (ListView)mView.findViewById(R.id.expenses_list);
        Date startDate = new Date(getEpoch(month, year));
        Date endDate = new Date(getEpoch(nextMonth(month), year));
        ArrayList<Expense> expenses = Filter.filterExpensesByDate(mCurrentBudget.getExpenses(), startDate, endDate);
        mExpenseAdapter = new ExpenseAdapter(mExpenseAdapter.mContext, expenses);
        listView.setAdapter(mExpenseAdapter);
        listView.invalidate();
    }

    private long getEpoch(int month, int year){ // milliseconds since January 1, 1970
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1);
        return calendar.getTimeInMillis();
    }

    private int nextMonth(int month){
        return (month == 12) ? 1 : month + 1;
    }

    private int previousMonth(int month){
        return (month == 1) ? 12 : month - 1;
    }

    public void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
    }
}
