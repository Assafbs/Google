package money.mezu.mezu;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class PieChartCategories implements GraphInterface {
    private PieChart mPieChart;
    private PieDataSet mPieDataSet;
    private Resources resources = staticContext.mContext.getResources();
    private String mTitle;
    private GraphEnum mGraphKind = GraphEnum.PIE_CHART;

    public PieChartCategories(String title) {
        mPieChart = null;
        mPieDataSet = null;
        mTitle = title;
    }

    public void setPieChart(PieChart pieChart) {
        mPieChart = pieChart;
    }

    public void setDataSet(PieDataSet pieDataSet) {
        mPieDataSet = pieDataSet;
    }

    public PieChart getPieChart() {
        return mPieChart;
    }

    @Override
    public GraphEnum getGraphKind() {
        return mGraphKind;
    }

    public PieDataSet getPieDataSet() {
        return mPieDataSet;
    }

    @Override
    public void calculateDataSet(Budget budget) {
        List<PieEntry> entries = new ArrayList<>();
        double amountPerCategory;

        for (Category category : Category.values()) {
            amountPerCategory = budget.getTotalExpensesPerCategory(category);
            if (amountPerCategory != 0) {
                entries.add(new PieEntry(((float) amountPerCategory), category.toString()));
            }
        }
        mPieDataSet = new PieDataSet(entries, "");
    }

    @Override
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

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void GenerateGraph(View view, Budget budget, boolean large) {
        calculateDataSet(budget);

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
        mPieChart.setNoDataText(resources.getString(R.string.no_data_chart));
        double totalExpenses = budget.getTotalExpenses();
        if (totalExpenses == 0) {
            mPieChart.setCenterText(resources.getString(R.string.no_data_chart));
        } else {
            mPieChart.setCenterText(resources.getString(R.string.expenses_sum) + "\n" + String.valueOf(totalExpenses));
        }
        mPieChart.setHoleRadius(45);
        mPieChart.setTransparentCircleRadius(50);
        mPieChart.setDrawSlicesUnderHole(true);
        mPieChart.setRotationEnabled(false);
        mPieChart.setEntryLabelTextSize(15);
        mPieChart.setCenterTextSize(15);
        mPieChart.getDescription().setEnabled(false);
        customizeLegend();

        if (!large) {
            mPieChart.getLegend().setEnabled(false);
            setSmallChart(data);
        }

        mPieChart.invalidate(); // refresh
    }

    public void setSmallChart(PieData data) {
        data.setValueTextSize(0);
        mPieChart.setNoDataText("");
        mPieChart.setCenterText("");
        mPieChart.setHoleRadius(0);
        mPieChart.setTransparentCircleRadius(0);
        mPieChart.setEntryLabelTextSize(0);
        mPieChart.setCenterTextSize(0);
        mPieChart.getLegend().setEnabled(false);
        mPieChart.setClickable(false);
        mPieChart.setTouchEnabled(false);
    }


    public int getColor(int color) {
        return ContextCompat.getColor(staticContext.mContext, color);
    }
}
