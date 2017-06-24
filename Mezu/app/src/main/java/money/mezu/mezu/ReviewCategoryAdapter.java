package money.mezu.mezu;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewCategoryAdapter extends ArrayAdapter<Category> {
    private Context mContext;
    private BudgetViewActivity mActivity;

    public ReviewCategoryAdapter(Context context, ArrayList<Category> categories) {
        super(context, 0, categories);
        mContext = context;
        mActivity = (BudgetViewActivity) context;
    }

    @Override
    public @NonNull
    View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Category category = getItem(position);
        final Budget budget = mActivity.mCurrentBudget;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_category, parent, false);
        }
        // Lookup view for data population

        TextView categoryName = (TextView) convertView.findViewById(R.id.categoryName);
        TextView sum = (TextView) convertView.findViewById(R.id.categotySum);
        final RelativeLayout categoryRow = (RelativeLayout) convertView.findViewById(R.id.categoryRow);
        categoryRow.setTag(category);

        // Populate the data into the template view using the data object
        categoryName.setText(category.toNiceString());

        double ceiling = mActivity.mCurrentBudget.tryGetCategoryCeiling(category);
        double categorySum = budget.getTotalExpenseOrIncomePerCategoryby(category, true);
        if (ceiling != -1){
            handleCategoryWithCeiling(categorySum, ceiling, categoryRow);
        } else {
            handleCategoryWithNoCeiling(categorySum, categoryRow);
        }

        categoryRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category cat = (Category) view.getTag();
                askForNewCeiling(cat, categoryRow);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void askForNewCeiling (final Category category, final RelativeLayout view) {
        new MaterialDialog.Builder(mContext)
                .title(R.string.enter_new_ceiling)
                .content(R.string.no_ceiling_wanted)
                .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER)
                .input(R.string.empty, R.string.empty, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        double ceiling;
                        String number = input.toString();
                        try {
                            if (number.equals(""))
                                ceiling = -1; //remove ceiling
                            else if(number.equals(".")) {
                                Toast.makeText(mActivity, mActivity.getString(R.string.not_a_valid_number), Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                            else
                                ceiling = Double.parseDouble(number);
                        } catch (NumberFormatException e) {
                            Toast.makeText(mActivity, mContext.getString(R.string.number_formating_failed), Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        changeCeilingForCategory(category, ceiling, view);
                    }
                }).show();
    }

    private void changeCeilingForCategory (Category category, double ceiling, RelativeLayout view) {
        Budget budget = mActivity.mCurrentBudget;
        budget.setCeilingForCategory(category, ceiling);
        FirebaseBackend.getInstance().editBudget(budget);
        double categorySum = budget.getTotalExpenseOrIncomePerCategoryby(category, true);
        if (ceiling != -1){
            handleCategoryWithCeiling(categorySum, ceiling, view);
        } else {
            handleCategoryWithNoCeiling(categorySum, view);
        }
    }

    private void handleCategoryWithCeiling (double catSum, double ceiling, RelativeLayout view) {
        RoundCornerProgressBar progress = (RoundCornerProgressBar) view.findViewById(R.id.progressBar);
        TextView sum = (TextView) view.findViewById(R.id.categotySum);
        sum.setText(String.valueOf(catSum) + " / " + String.valueOf(ceiling));
        if (LanguageUtils.isRTL()) {
            progress.setReverse(true);
        }
        float precentage = (float)(catSum/ceiling);
        progress.setProgress(precentage < 1 ? precentage * 100 : 100);
        if (precentage<0.6){
            progress.setProgressColor(Color.parseColor("#888bc34a"));
        } else if (precentage <= 1) {
            progress.setProgressColor(Color.parseColor("#88ffc000"));
        } else {
            progress.setProgressColor(Color.parseColor("#88f44336"));
        }
    }

    private void handleCategoryWithNoCeiling(double catSum, RelativeLayout view) {
        TextView sum = (TextView) view.findViewById(R.id.categotySum);
        sum.setText(String.valueOf(catSum));
        RoundCornerProgressBar progress = (RoundCornerProgressBar) view.findViewById(R.id.progressBar);
        progress.setProgress(0);
    }

}
