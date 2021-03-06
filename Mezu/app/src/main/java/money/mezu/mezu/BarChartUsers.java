package money.mezu.mezu;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;


public class BarChartUsers implements GraphInterface {
    private static final int VALUE_TEXT_SIZE = 10;
    private static final float BAR_WIDTH = 0.9f;

    private BarChart mBarChart;
    private BarDataSet mBarDataSet;
    private ArrayList<String> mUsers;
    private Resources resources = StaticContext.mContext.getResources();
    private String mTitle;
    private String mInfoLine;
    private String mInfoValue;
    private Budget mBudget;
    private GraphEnum mGraphKind = GraphEnum.BAR_CHART;

    public BarChartUsers(Budget budget) {
        mBarChart = null;
        mBarDataSet = null;
        mUsers = null;
        mTitle = resources.getString(R.string.expenses_by_users);
        mInfoLine = resources.getString(R.string.most_expensive_user);
        mBudget = budget;
        mInfoValue = mBudget.getTotalExpenses() == 0 ? resources.getString(R.string.not_enough_data) : mBudget.getMostExpensiveUser();
    }

    public void setBarChart(BarChart barChart) {
        mBarChart = barChart;
    }

    public void setBarDataSet(BarDataSet barDataSet) {
        mBarDataSet = barDataSet;
    }

    public BarChart getBarChart() {
        return mBarChart;
    }

    @Override
    public GraphEnum getGraphKind() {
        return mGraphKind;
    }

    public BarDataSet getBarDataSet() {
        return mBarDataSet;
    }

    @Override
    public void calculateDataSet() {
        List<BarEntry> entries = new ArrayList<>();
        mUsers = mBudget.getArrayOfUserNamesExpensesOnly();
        double amountPerUser;
        int i = 0;
        for (String user : mUsers) {
            amountPerUser = mBudget.getAmountPerUserName(user);
            entries.add(new BarEntry(i, (float) amountPerUser));
            i++;
        }
        mBarDataSet = new BarDataSet(entries, resources.getString(R.string.expenses));
    }

    @Override
    public void customizeLegend() {
        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);
    }

    private void customizeAxis() {
        YAxis yAxisRight = mBarChart.getAxisRight();
        YAxis yAxisLeft = mBarChart.getAxisLeft();
        XAxis xAxis = mBarChart.getXAxis();

        yAxisRight.setEnabled(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return getFirstName(mUsers.get((int) value));
            }
        });
    }

    private String getFirstName(String name) {
        String[] l = name.split(" ");
        if (l.length > 1) {
            return l[0] + " " + l[1].charAt(0);
        } else {
            return l[0];
        }
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

        final int[] MY_COLORS = {getColor(R.color.pie_turquoise),
                getColor(R.color.pie_sky_blue),
                getColor(R.color.pie_blue),
                getColor(R.color.pie_dark_blue)};

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : MY_COLORS) colors.add(c);
        mBarDataSet.setColors(colors);

        BarData data = new BarData(mBarDataSet);
        data.setValueTextSize(VALUE_TEXT_SIZE);
        data.setBarWidth(BAR_WIDTH);

        mBarChart.setData(data);
        mBarChart.setFitBars(true);

        double totalExpenses = mBudget.getTotalExpenses();
        if (totalExpenses == 0) {
            mBarChart.setVisibility(View.INVISIBLE);
        }
        mBarChart.setNoDataText(resources.getString(R.string.no_data_chart));
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(true);
        mBarChart.setAutoScaleMinMaxEnabled(true);

        customizeAxis();
        customizeLegend();
        if (!large) {
            mBarChart.getLegend().setEnabled(false);
            setSmallChart(data);
        }
        mBarChart.invalidate(); // refresh
    }

    private void setSmallChart(BarData data) {
        data.setValueTextSize(0);
        mBarChart.setNoDataText("");
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setClickable(false);
        mBarChart.setTouchEnabled(false);
        mBarChart.setLogEnabled(false);
        mBarChart.getAxisLeft().setEnabled(false);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getXAxis().setEnabled(false);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(StaticContext.mContext, color);
    }
}
