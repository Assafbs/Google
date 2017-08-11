package money.mezu.mezu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;

public class ReviewTabFragment extends Fragment implements BudgetUpdatedListener {

    private BudgetViewActivity mActivity;
    private View mView = null;
    private ReviewCategoryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_review, container, false);
        mActivity = (BudgetViewActivity) getActivity();

        ListView catList = (ListView) mView.findViewById(R.id.categories_expenses);
        ArrayList<Category> catArray = Category.getExpenseCategoriesList();
        mAdapter = new ReviewCategoryAdapter(mActivity, catArray);
        catList.setAdapter(mAdapter);

        setupInfoButton();
        setupBudgetOverall();

        View overallFrame = mView.findViewById(R.id.overallFrame);
        overallFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForNewCeiling();
            }
        });
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
        setNoExpensesIndication();
        return mView;
    }

    private void askForNewCeiling() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.enter_new_ceiling)
                .content(R.string.no_ceiling_wanted)
                .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER)
                .input(R.string.empty, R.string.empty, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        double ceiling;
                        String number = input.toString();
                        try {
                            switch (number) {
                                case "":
                                    ceiling = -1; //remove ceiling
                                    break;
                                case ".":
                                    Toast.makeText(mActivity, mActivity.getString(R.string.not_a_valid_number), Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                default:
                                    ceiling = Double.parseDouble(number);
                                    break;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(mActivity, mActivity.getString(R.string.number_formating_failed), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        changeBudgetCeiling(ceiling);
                    }
                }).show();
    }

    private void changeBudgetCeiling(double ceiling) {
        Budget budget = mActivity.mCurrentBudget;
        budget.setCeilingForCategory(Category.CATEGORY, ceiling);
        FirebaseBackend.getInstance().editBudget(budget);
        setupBudgetOverall();
    }

    private void setupInfoButton() {
        final ImageView info = (ImageView) mView.findViewById(R.id.info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupWindow popup = new PopupWindow(mActivity);
                View layout = mActivity.getLayoutInflater().inflate(R.layout.review_info_box, null);
                popup.setContentView(layout);
                popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                popup.showAsDropDown(info);
            }
        });
    }

    private void setupBudgetOverall() {
        RoundCornerProgressBar progressBar = (RoundCornerProgressBar) mView.findViewById(R.id.budgetProgress);
        TextView budgetSum = (TextView) mView.findViewById(R.id.budgetSum);
        if (LanguageUtils.isRTL()) {
            progressBar.setReverse(false);
        }
        double ceiling = mActivity.mCurrentBudget.tryGetCategoryCeiling(Category.CATEGORY);
        double sum = mActivity.mCurrentBudget.getTotalExpenses();
        if (ceiling != -1) {
            budgetSum.setText(String.valueOf(sum) + " / " + String.valueOf(ceiling));
            float percentage = (float) (sum / ceiling);
            progressBar.setProgress(percentage < 1 ? percentage * 100 : 100);
            if (percentage < 0.6) {
                progressBar.setProgressColor(Color.parseColor("#888bc34a"));
            } else if (percentage <= 1) {
                progressBar.setProgressColor(Color.parseColor("#88ffc000"));
            } else {
                progressBar.setProgressColor(Color.parseColor("#88f44336"));
            }
        } else {
            budgetSum.setText(String.valueOf(sum));
            progressBar.setProgress(0);
        }
    }

    @Override
    public void budgetUpdatedCallback(Budget newBudget) {
        mAdapter.notifyDataSetChanged();
        setupBudgetOverall();
    }

    private void setNoExpensesIndication() {
        if (mActivity.mCurrentBudget.getExpenses().size() == 0) {
            mView.findViewById(R.id.review_top_layout).setVisibility(View.GONE);
            mView.findViewById(R.id.categories_expenses).setVisibility(View.GONE);
            mView.findViewById(R.id.review_bottom_layout).setVisibility(View.GONE);
            mView.findViewById(R.id.explaining_text3).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.crying_logo).setVisibility(View.VISIBLE);
        } else {
            mView.findViewById(R.id.review_top_layout).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.categories_expenses).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.review_bottom_layout).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.explaining_text3).setVisibility(View.GONE);
            mView.findViewById(R.id.crying_logo).setVisibility(View.GONE);
        }
    }
}
