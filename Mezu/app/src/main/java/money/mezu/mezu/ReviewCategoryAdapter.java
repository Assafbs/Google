package money.mezu.mezu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

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

        double ceiling = tryGetCategoryCeiling(category);
        double categorySum = budget.getTotalExpenseOrIncomePerCategoryby(category, true);
        if (ceiling != -1){
            handleCategoryWithCeiling(categorySum, ceiling, categoryRow);
        } else {
            sum.setText(String.valueOf(categorySum));
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
                .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER)
                .input(R.string.empty, R.string.empty, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        double ceiling;
                        try {
                            ceiling = Double.parseDouble(input.toString());
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
        //((BaseAdapter)this).notifyDataSetChanged();
        double categorySum = budget.getTotalExpenseOrIncomePerCategoryby(category, true);
        handleCategoryWithCeiling(categorySum, ceiling, view);
    }

    private double tryGetCategoryCeiling (Category category) {
        Budget budget = mActivity.mCurrentBudget;
        HashMap<Category, Double> categoryCeilings = budget.getCategoryCeilings();
        if (categoryCeilings == null) {
            return -1;
        }
        Double ceiling = categoryCeilings.get(category) == null ?
                null :
                ((Number) categoryCeilings.get(category)).doubleValue();
        return ceiling == null ? -1 : ceiling;
    }

    private void handleCategoryWithCeiling (double catSum, double ceiling, RelativeLayout view) {
        LinearLayout progress = (LinearLayout) view.findViewById(R.id.progressBar);
        TextView sum = (TextView) view.findViewById(R.id.categotySum);
        sum.setText(String.valueOf(catSum) + " / " + String.valueOf(ceiling));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)progress.getLayoutParams();
        params.weight = (float)Math.floor((catSum / ceiling)*1000);
    }

}
