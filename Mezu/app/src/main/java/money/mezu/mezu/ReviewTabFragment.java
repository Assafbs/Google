package money.mezu.mezu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ReviewTabFragment extends Fragment implements ExpenseUpdatedListener, BudgetUpdatedListener{

    private BudgetViewActivity mActivity;
    private View mView = null;
    private ReviewCategoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_review, container, false);
        mActivity = (BudgetViewActivity) getActivity();

        EventDispatcher.getInstance().registerExpenseUpdateListener(this);

        ListView catList = (ListView) mView.findViewById(R.id.categories_expenses);
        ArrayList<Category> catArray = Category.getExpenseCategoriesList();
        mAdapter = new ReviewCategoryAdapter(mActivity, catArray);
        catList.setAdapter(mAdapter);

        return mView;
    }

    @Override
    public void expenseUpdatedCallback() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void budgetUpdatedCallback(Budget newBudget) {
        mAdapter.notifyDataSetChanged();
    }
}
