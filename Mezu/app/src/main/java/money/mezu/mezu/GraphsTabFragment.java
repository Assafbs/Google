package money.mezu.mezu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class GraphsTabFragment extends Fragment implements ExpenseUpdatedListener, BudgetUpdatedListener {

    private View mView = null;
    private GraphAdapter mGraphAdapter = null;
    private ListView mListView;
    private BudgetViewActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_graphs, container, false);
        mActivity = (BudgetViewActivity) getActivity();
        mListView = (ListView) mView.findViewById(R.id.graphs_list);
        refreshGraphAdapter();
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
        setNoExpensesIndication();
        return mView;
    }

    public ArrayList<GraphInterface> getArrayOfGraphs() {
        Budget budget = mActivity.mCurrentBudget;
        ArrayList<GraphInterface> graphArray = new ArrayList<>();
        PieChartCategories pieChartCategories = new PieChartCategories(budget);
        LineChartMonths lineChartMonths = new LineChartMonths(budget);
        BarChartUsers barChartUsers = new BarChartUsers(budget);

        //ADD GRAPHS HERE! ONE PER EACH GRAPH CREATED
        if (budget.getTotalExpenses() > 0) {
            graphArray.add(pieChartCategories);
        }
        if (budget.getTotalExpenses() > 0 || budget.getTotalIncomes() > 0) {
            graphArray.add(lineChartMonths);
        }
        if (budget.getTotalExpenses() > 0 && budget.getArrayOfUserNamesExpensesOnly().size() > 1) {
            graphArray.add(barChartUsers);
        }
        return graphArray;
    }

    @Override
    public void budgetUpdatedCallback(Budget newBudget) {
        refreshGraphAdapter();
        setNoExpensesIndication();
    }

    @Override
    public void expenseUpdatedCallback() {
        refreshGraphAdapter();
        setNoExpensesIndication();
    }

    public void refreshGraphAdapter() {
        // Create the adapter to convert the array to views
        mGraphAdapter = new GraphAdapter(mActivity, getArrayOfGraphs());
        // Attach the adapter to a ListView
        mListView.setAdapter(mGraphAdapter);
    }

    private void setNoExpensesIndication() {
        if (mActivity.mCurrentBudget.getExpenses().size() == 0) {
            mView.findViewById(R.id.graphs_list).setVisibility(View.GONE);
            mView.findViewById(R.id.explaining_text2).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.crying_logo).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.graphs_list).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.explaining_text2).setVisibility(View.GONE);
            mView.findViewById(R.id.crying_logo).setVisibility(View.GONE);
        }
    }
}
