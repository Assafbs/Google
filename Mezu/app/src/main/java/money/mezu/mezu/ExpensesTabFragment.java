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
import java.util.Collections;
import java.util.Date;

public class ExpensesTabFragment extends Fragment implements ExpenseUpdatedListener {

    private BudgetViewActivity mActivity;
    private ExpenseAdapter mExpenseAdapter = null;
    private View mView = null;
    public static boolean sDefaultDate = true;
    private int mMonth;
    private int mYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_expenses, container, false);
        mActivity = (BudgetViewActivity) getActivity();

        EventDispatcher.getInstance().registerExpenseUpdateListener(this);

        setupMonthSelection();
        filterExpenses(mMonth, mYear);
        return mView;
    }


    public void expenseUpdatedCallback() {
        Log.d("", "ExpensesTabFragment:expenseUpdatedCallback: invoked");
        for (Expense expense : mActivity.mCurrentBudget.getExpenses()) {
            Log.d("", String.format("ExpensesTabFragment:expenseUpdatedCallback: has expense: %s", expense.getTitle()));
        }
        filterExpenses(mMonth, mYear);
    }

    private void setupMonthSelection() {
        if (sDefaultDate || mActivity.mYear == 0) {
            Calendar calendar = Calendar.getInstance();
            setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } else {
            setMonth(mActivity.mMonth + 1, mActivity.mYear);
        }
        ImageView nextButton = (ImageView) mView.findViewById(R.id.next_arrow);
        ImageView backButton = (ImageView) mView.findViewById(R.id.back_arrow);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = nextMonth(mMonth);
                int year = (month == 1) ? mYear + 1 : mYear;
                setMonth(month, year);
                sDefaultDate = false;
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = previousMonth(mMonth);
                int year = (month == 12) ? mYear - 1 : mYear;
                setMonth(month, year);
                sDefaultDate = true;
            }
        });
    }

    private void setMonth(int month, int year) {
        mMonth = month;
        mYear = year;
        mActivity.mMonth = mMonth - 1;
        mActivity.mYear = mYear;

        TextView currentMonthTextView = (TextView) mView.findViewById(R.id.current_month);
        currentMonthTextView.setText(mMonth + "/" + mYear);
        filterExpenses(mMonth, mYear);
    }

    public void filterExpenses() {
        filterExpenses(mMonth, mYear);
    }

    private void filterExpenses(int month, int year) {
        if (mView == null)
            return;
        ListView listView = (ListView) mView.findViewById(R.id.expenses_list);
        Date startDate = new Date(getEpoch(month, year));
        month = nextMonth(month);
        year = month == 1 ? year + 1 : year;
        Date endDate = new Date(getEpoch(month, year));
        ArrayList<Expense> expenses = Filter.filterExpensesByDate(mActivity.mCurrentBudget.getExpenses(), startDate, endDate);
        Collections.sort(expenses);
        mExpenseAdapter = new ExpenseAdapter(mActivity, expenses);
        listView.setAdapter(mExpenseAdapter);
        listView.invalidate();
    }


    private long getEpoch(int month, int year) { // milliseconds since January 1, 1970
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1);
        return calendar.getTimeInMillis();
    }

    private int nextMonth(int month) {
        return (month == 12) ? 1 : month + 1;
    }

    private int previousMonth(int month) {
        return (month == 1) ? 12 : month - 1;
    }

}
