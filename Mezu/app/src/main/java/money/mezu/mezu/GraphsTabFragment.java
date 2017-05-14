package money.mezu.mezu;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import static money.mezu.mezu.BudgetViewActivity.mCurrentBudget;
import static money.mezu.mezu.BudgetViewActivity.mPieChart;
import static money.mezu.mezu.BudgetViewActivity.mPieDataSet;


public class GraphsTabFragment extends Fragment {

    private View mView = null;
    private static Resources resources = staticContext.mContext.getResources();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_graphs, container, false);


        mPieChart = (PieChart) mView.findViewById(R.id.pie_chart);

        calculatePieDataSet();

        //THIS DOESNT WORK
        final int[] MY_COLORS = {R.color.pie_red, R.color.pie_orange, R.color.pie_yellow, R.color.pie_green,
                R.color.pie_turquoise, R.color.pie_sky_blue, R.color.pie_blue, R.color.pie_dark_blue,
                R.color.pie_purple, R.color.pie_pink, R.color.pie_fuchsia, R.color.pie_dark_red,
                R.color.pie_beige, R.color.pie_dark_green, R.color.pie_brown, R.color.pie_light_green};

        //THIS NEEDS TO BE 16 COLORS AND CHOOSE NICER COLORS!
        final int[] MY_COLORS2 = {Color.rgb(192, 0, 0), Color.rgb(255, 0, 0), Color.rgb(255, 192, 0),
                Color.rgb(127, 127, 127), Color.rgb(146, 208, 80), Color.rgb(0, 176, 80), Color.rgb(79, 129, 189)};

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : MY_COLORS2) colors.add(c);

        mPieDataSet.setColors(colors);

        PieData data = new PieData(mPieDataSet);
        mPieChart.setData(data);
        Description desc = new Description();
        desc.setText(resources.getString(R.string.expenses_per_categories));
        mPieChart.setDescription(desc);
        mPieChart.setNoDataText(resources.getString(R.string.no_data_pie));
        double totalExpenses = mCurrentBudget.getTotalExpenses();
        if (totalExpenses == 0) {
            mPieChart.setCenterText(resources.getString(R.string.no_data_pie));
        }
        mPieChart.setHoleRadius(45);
        mPieChart.setTransparentCircleRadius(50);
        mPieChart.setDrawSlicesUnderHole(true);
        mPieChart.setRotationEnabled(false);
        mPieChart.setCenterText(getString(R.string.expenses_sum) + String.valueOf(totalExpenses));
        mPieChart.invalidate(); // refresh

        return mView;
    }

    public static void calculatePieDataSet() {
        List<PieEntry> entries = new ArrayList<>();
        double amountPerCategory;

        for (Category category : Category.values()) {
            amountPerCategory = mCurrentBudget.getTotalExpensesPerCategory(category);
            if (amountPerCategory != 0) {
                entries.add(new PieEntry(((float) amountPerCategory), category.toString()));
            }
        }
        mPieDataSet = new PieDataSet(entries, resources.getString(R.string.categories));
    }


}
