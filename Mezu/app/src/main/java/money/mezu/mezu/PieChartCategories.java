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
    private Resources resources = StaticContext.mContext.getResources();
    private String mTitle;
    private String mInfoLine;
    private String mInfoValue;
    private Budget mBudget;
    private GraphEnum mGraphKind = GraphEnum.PIE_CHART;

    public PieChartCategories(Budget budget) {
        mPieChart = null;
        mPieDataSet = null;
        mTitle = resources.getString(R.string.expenses_by_categories);
        mInfoLine = resources.getString(R.string.most_spending_on);
        mBudget = budget;
        mInfoValue = mBudget.getTotalExpenses() == 0 ? resources.getString(R.string.not_enough_data) : mBudget.getMostExpensiveCategory().toNiceString();
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
    public void calculateDataSet() {
        List<PieEntry> entries = new ArrayList<>();
        double amountPerCategory;

        for (Category category : Category.values()) {
            amountPerCategory = mBudget.getTotalExpensesPerCategory(category);
            if (amountPerCategory != 0) {
                entries.add(new PieEntry(((float) amountPerCategory), category.toNiceString()));
            }
        }
        mPieDataSet = new PieDataSet(entries, "");
    }

    @Override
    public void customizeLegend() {
        Legend legend = mPieChart.getLegend();
        if (LanguageUtils.isRTL()) {
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
        legend.setWordWrapEnabled(true);
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getInfoLine() {
        return mInfoLine;
    }

    @Override
    public String getInfoValue() {
        return mInfoValue;
    }

    @Override
    public void GenerateGraph(View view, boolean large) {
        calculateDataSet();

        final int[] MY_COLORS = {getColor(R.color.pie_red), getColor(R.color.pie_orange),
                getColor(R.color.pie_yellow), getColor(R.color.pie_green),
                getColor(R.color.pie_turquoise), getColor(R.color.pie_sky_blue),
                getColor(R.color.pie_blue), getColor(R.color.pie_dark_blue),
                getColor(R.color.pie_purple), getColor(R.color.pie_pink),
                getColor(R.color.pie_fuchsia), getColor(R.color.pie_dark_red),
                getColor(R.color.pie_beige), getColor(R.color.pie_dark_green),
                getColor(R.color.pie_brown), getColor(R.color.pie_light_green)};

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : MY_COLORS) colors.add(c);
        mPieDataSet.setColors(colors);
        PieData data = new PieData(mPieDataSet);
        data.setValueTextSize(15);

        mPieChart.setData(data);
        if (mPieDataSet.getEntryCount() > 5) {
            mPieChart.setDrawEntryLabels(false);
        } else {
            mPieChart.setDrawEntryLabels(true);
        }

        double totalExpenses = mBudget.getTotalExpenses();
        if (totalExpenses == 0) {
            mPieChart.setVisibility(View.INVISIBLE);
        } else {
            mPieChart.setCenterText(resources.getString(R.string.expenses_sum) + "\n" + String.valueOf(totalExpenses));
        }
        mPieChart.setNoDataText(resources.getString(R.string.no_data_chart));
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

    private void setSmallChart(PieData data) {
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


    private int getColor(int color) {
        return ContextCompat.getColor(StaticContext.mContext, color);
    }
}
