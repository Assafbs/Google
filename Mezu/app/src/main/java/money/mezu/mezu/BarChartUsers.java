package money.mezu.mezu;

import android.content.res.Resources;
import android.graphics.Paint;
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
    private BarChart mBarChart;
    private BarDataSet mBarDataSet;
    private ArrayList<String> mUsers;
    private Resources resources = staticContext.mContext.getResources();
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
        mInfoValue = budget.getMostExpensiveUser();
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

    public void customizeAxis() {
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

    public String getFirstName(String name) {
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

        final int[] MY_COLORS = {getColor(R.color.pie_turquoise), getColor(R.color.pie_sky_blue), getColor(R.color.pie_blue), getColor(R.color.pie_dark_blue)};

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : MY_COLORS) colors.add(c);
        mBarDataSet.setColors(colors);

        BarData data = new BarData(mBarDataSet);
        data.setValueTextSize(10);
        data.setBarWidth(0.9f);

        mBarChart.setData(data);
        mBarChart.setFitBars(true);
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

    public void setSmallChart(BarData data) {
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


    public int getColor(int color) {
        return ContextCompat.getColor(staticContext.mContext, color);
    }
}
