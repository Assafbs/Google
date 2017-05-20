package money.mezu.mezu;


import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class GraphsTabFragment extends Fragment implements ExpenseUpdatedListener {

    private View mView = null;
    private static Resources resources = staticContext.mContext.getResources();
    protected Budget mCurrentBudget;
    protected PieChart mPieChart;
    protected PieDataSet mPieDataSet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_graphs, container, false);
        GenerateGraph();
        EventDispatcher.getInstance().registerExpenseUpdateListener(this);
        return mView;
    }

    private void GenerateGraph() {
        mPieChart = (PieChart) mView.findViewById(R.id.pie_chart);
        calculatePieDataSet();

        //I MADE IT WORK
        final int[] MY_COLORS = {getColor(R.color.pie_red), getColor(R.color.pie_orange), getColor(R.color.pie_yellow), getColor(R.color.pie_green),
                getColor(R.color.pie_turquoise), getColor(R.color.pie_sky_blue), getColor(R.color.pie_blue), getColor(R.color.pie_dark_blue),
                getColor(R.color.pie_purple), getColor(R.color.pie_pink), getColor(R.color.pie_fuchsia), getColor(R.color.pie_dark_red),
                getColor(R.color.pie_beige), getColor(R.color.pie_dark_green), getColor(R.color.pie_brown), getColor(R.color.pie_light_green)};

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : MY_COLORS) colors.add(c);
        mPieDataSet.setColors(colors);
        PieData data = new PieData(mPieDataSet);
        data.setValueTextSize(15);
        mPieChart.setData(data);
        Description desc = new Description();

        desc.setText(resources.getString(R.string.expenses_per_categories));
        mPieChart.setDescription(desc);
        mPieChart.setNoDataText(resources.getString(R.string.no_data_pie));
        double totalExpenses = mCurrentBudget.getTotalExpenses();
        if (totalExpenses == 0) {
            mPieChart.setCenterText(resources.getString(R.string.no_data_pie));
        }else{
            mPieChart.setCenterText(resources.getString(R.string.expenses_sum) + "\n" +  String.valueOf(totalExpenses));
        }
        mPieChart.setHoleRadius(45);
        mPieChart.setTransparentCircleRadius(50);
        mPieChart.setDrawSlicesUnderHole(true);
        mPieChart.setRotationEnabled(false);
        mPieChart.setEntryLabelTextSize(15);
        mPieChart.setCenterTextSize(15);
        customizeLegend();
        mPieChart.invalidate(); // refresh
    }

    public void expenseUpdatedCallback() {
        GenerateGraph();
    }

    public void customizeLegend() {
        Legend legend = mPieChart.getLegend();
        if (BudgetViewActivity.isRTL()) {
            legend.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        } else {
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        }
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setFormSize(10f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);
    }

    public void calculatePieDataSet() {
        List<PieEntry> entries = new ArrayList<>();
        double amountPerCategory;

        for (Category category : Category.values()) {
            amountPerCategory = mCurrentBudget.getTotalExpensesPerCategory(category);
            if (amountPerCategory != 0) {
                entries.add(new PieEntry(((float) amountPerCategory), category.toString()));
            }
        }
        mPieDataSet = new PieDataSet(entries, "");
    }

    public void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
    }

    public int getColor(int color) {
        return ContextCompat.getColor(staticContext.mContext, color);
    }
}
