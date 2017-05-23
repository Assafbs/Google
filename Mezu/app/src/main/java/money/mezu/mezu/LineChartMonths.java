package money.mezu.mezu;

import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LineChartMonths implements GraphInterface {
    private LineChart mLineChart;
    private LineDataSet mLineDataSet;
    private Resources resources = staticContext.mContext.getResources();
    private String mTitle;
    private GraphEnum mGraphKind = GraphEnum.LINE_CHART;
    private int mMonth;
    private int mYear;

    public LineChartMonths(String title) {
        mLineChart = null;
        mLineDataSet = null;
        mTitle = title;
    }

    public void setLineChart(LineChart lineChart) {
        mLineChart = lineChart;
    }

    public void setDataSet(LineDataSet lineDataSet) {
        mLineDataSet = lineDataSet;
    }

    public LineChart getLineChart() {
        return mLineChart;
    }

    @Override
    public GraphEnum getGraphKind() {
        return mGraphKind;
    }

    public LineDataSet getLineDataSet() {
        return mLineDataSet;
    }

    @Override
    public void calculateDataSet(Budget budget) {
        List<Entry> entries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        double amountPerMonth;
        for (int i = 1; i <= 12; i++) {
            Date startDate = new Date(getEpoch(i, year));
            Date endDate = new Date(getEpoch(nextMonth(i), year));
            ArrayList<Expense> expenses_by_months = Filter.filterExpensesByDate(budget.getExpenses(), startDate, endDate);
            amountPerMonth = getTotalExpensesAmount(expenses_by_months);
            entries.add(new Entry(i, (float) amountPerMonth));
        }
        mLineDataSet = new LineDataSet(entries, "");
    }

    private double getTotalExpensesAmount(ArrayList<Expense> array_of_expenses) {
        double acc = 0;
        for (Expense expense : array_of_expenses) {
            acc += expense.getAmount();
        }
        return acc;
    }

    private int nextMonth(int month) {
        return (month == 12) ? 1 : month + 1;
    }

    private long getEpoch(int month, int year) { // milliseconds since January 1, 1970
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1);
        return calendar.getTimeInMillis();
    }

    @Override
    public void customizeLegend() {
        Legend legend = mLineChart.getLegend();
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

    public void setAxis() {
        YAxis yAxisRight = mLineChart.getAxisRight();
        YAxis yAxisLeft = mLineChart.getAxisLeft();
        XAxis xAxis = mLineChart.getXAxis();

        yAxisRight.setEnabled(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(12, true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // "value" represents the position of the label on the axis (x or y)
                return resources.getStringArray(R.array.months_list)[(int) value - 1];
            }
        });
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void GenerateGraph(View view, Budget budget, boolean large) {
        calculateDataSet(budget);

        //I MADE IT WORK
        final int[] MY_COLORS = {getColor(R.color.pie_red)};

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : MY_COLORS) colors.add(c);
        mLineDataSet.setColors(colors);
        LineData data = new LineData(mLineDataSet);
        data.setValueTextSize(15);
        mLineChart.setData(data);
        mLineChart.setNoDataText(resources.getString(R.string.no_data_chart));
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setPinchZoom(true);


        setAxis();

        customizeLegend();
        if (!large) {
            mLineChart.getLegend().setEnabled(false);
            setSmallChart(data);
        }

        mLineChart.invalidate(); // refresh
    }

    public void setSmallChart(LineData data) {
        data.setValueTextSize(0);
        mLineChart.setNoDataText("");
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setClickable(false);
        mLineChart.setTouchEnabled(false);
        mLineChart.setLogEnabled(false);

        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getXAxis().setEnabled(false);
    }


    public int getColor(int color) {
        return ContextCompat.getColor(staticContext.mContext, color);
    }
}
