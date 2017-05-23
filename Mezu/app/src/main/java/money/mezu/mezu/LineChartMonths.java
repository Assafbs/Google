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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LineChartMonths implements GraphInterface {
    private LineChart mLineChart;
    private LineDataSet mLineDataSet1;
    private LineDataSet mLineDataSet2;
    private Resources resources = staticContext.mContext.getResources();
    private String mTitle;
    private GraphEnum mGraphKind = GraphEnum.LINE_CHART;
    private int mMonth;
    private int mYear;

    public LineChartMonths(String title) {
        mLineChart = null;
        mLineDataSet1 = null;
        mLineDataSet2 = null;
        mTitle = title;
    }

    public void setLineChart(LineChart lineChart) {
        mLineChart = lineChart;
    }

    public void setDataSet1(LineDataSet lineDataSet) {
        mLineDataSet1 = lineDataSet;
    }

    public void setDataSet2(LineDataSet lineDataSet) {
        mLineDataSet2 = lineDataSet;
    }

    public LineChart getLineChart() {
        return mLineChart;
    }

    @Override
    public GraphEnum getGraphKind() {
        return mGraphKind;
    }

    public LineDataSet getLineDataSet1() {
        return mLineDataSet1;
    }

    public LineDataSet getLineDataSet2() {
        return mLineDataSet2;
    }

    @Override
    public void calculateDataSet(Budget budget) {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        double amountPerMonth;

        for (int i = 1; i <= 12; i++) {
            Date startDate = new Date(getEpoch(i, year));
            Date endDate = new Date(getEpoch(nextMonth(i), year));
            ArrayList<Expense> expenses_by_months = Filter.filterExpensesByDate(budget.getExpenses(), startDate, endDate);
            amountPerMonth = getTotalExpensesOrIncomesAmount(expenses_by_months, true);
            entries.add(new Entry(i, (float) amountPerMonth));
        }
        mLineDataSet1 = new LineDataSet(entries, resources.getString(R.string.expenses));

//        entries.clear();

        for (int i = 1; i <= 12; i++) {
            Date startDate = new Date(getEpoch(i, year));
            Date endDate = new Date(getEpoch(nextMonth(i), year));
            ArrayList<Expense> expenses_by_months = Filter.filterExpensesByDate(budget.getExpenses(), startDate, endDate);
            amountPerMonth = getTotalExpensesOrIncomesAmount(expenses_by_months, false);
            entries2.add(new Entry(i, (float) amountPerMonth));
        }
        mLineDataSet2 = new LineDataSet(entries2, resources.getString(R.string.incomes));
    }


    private double getTotalExpensesOrIncomesAmount(ArrayList<Expense> array_of_expenses, boolean isExpenses) {
        double acc = 0;
        for (Expense expense : array_of_expenses) {
            if (expense.getIsExpense() == isExpenses) {
                acc += expense.getAmount();
            }
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

        mLineDataSet1.setColor(getColor(R.color.pie_red));
        mLineDataSet2.setColor(getColor((R.color.pie_blue)));
        mLineDataSet2.setValueTextColor(getColor(R.color.pie_dark_green));
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineDataSet1);
        dataSets.add(mLineDataSet2);
        LineData data = new LineData(dataSets);
        data.setValueTextSize(10);
//        data.setDrawValues(false);

        mLineChart.setData(data);
        mLineChart.setNoDataText(resources.getString(R.string.no_data_chart));
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setPinchZoom(true);
        mLineChart.setAutoScaleMinMaxEnabled(true);

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
