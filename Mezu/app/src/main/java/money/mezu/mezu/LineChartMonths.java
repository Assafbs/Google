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
    private LineDataSet mLineDataSetExpenses;
    private LineDataSet mLineDataSetIncomes;
    private Resources resources = StaticContext.mContext.getResources();
    private String mTitle;
    private String mInfoLine;
    private String mInfoValue;
    private Budget mBudget;
    private GraphEnum mGraphKind = GraphEnum.LINE_CHART;
    private int mMonth;
    private int mYear;

    public LineChartMonths(Budget budget) {
        mLineChart = null;
        mLineDataSetExpenses = null;
        mTitle = resources.getString(R.string.expenses_by_months);
        mInfoLine = resources.getString(R.string.most_expensive_month);
        mBudget = budget;
        mInfoValue = mBudget.getExpenses().isEmpty() ? resources.getString(R.string.not_enough_data) :
                resources.getStringArray(R.array.months_list)[budget.getMostExpensiveMonthPerYear(Calendar.getInstance().get(Calendar.YEAR))];
    }

    public void setLineChart(LineChart lineChart) {
        mLineChart = lineChart;
    }

    public void setDataSetExpenses(LineDataSet lineDataSet) {
        mLineDataSetExpenses = lineDataSet;
    }

    public void setDataSetIncomes(LineDataSet lineDataSet) {
        mLineDataSetIncomes = lineDataSet;
    }

    public LineChart getLineChart() {
        return mLineChart;
    }

    @Override
    public GraphEnum getGraphKind() {
        return mGraphKind;
    }

    public LineDataSet getLineDataSetExpenses() {
        return mLineDataSetExpenses;
    }

    public LineDataSet getLineDataSetIncomes() {
        return mLineDataSetIncomes;
    }

    @Override
    public void calculateDataSet() {
        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        ArrayList<Expense> expenses_by_months = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        Date startDate;
        Date endDate;
        int year = c.get(Calendar.YEAR);
        double amountPerMonth;

        for (int i = 1; i <= 12; i++) {
            startDate = new Date(getEpoch(i, year));
            endDate = new Date(getEpoch(nextMonth(i), year));
            expenses_by_months = Filter.filterExpensesByDate(mBudget.getExpenses(), startDate, endDate);
            amountPerMonth = getTotalExpensesOrIncomesAmount(expenses_by_months, true);
            entries.add(new Entry(i, (float) amountPerMonth));
        }
        mLineDataSetExpenses = new LineDataSet(entries, resources.getString(R.string.expenses));

        for (int i = 1; i <= 12; i++) {
            startDate = new Date(getEpoch(i, year));
            endDate = new Date(getEpoch(nextMonth(i), year));
            expenses_by_months = Filter.filterExpensesByDate(mBudget.getExpenses(), startDate, endDate);
            amountPerMonth = getTotalExpensesOrIncomesAmount(expenses_by_months, false);
            entries2.add(new Entry(i, (float) amountPerMonth));
        }
        mLineDataSetIncomes = new LineDataSet(entries2, resources.getString(R.string.incomes));
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
        legend.setWordWrapEnabled(true);
    }

    public void customizeAxis() {
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
        mLineDataSetExpenses.setColor(getColor(R.color.pie_red));
        mLineDataSetIncomes.setColor(getColor((R.color.pie_blue)));
        mLineDataSetIncomes.setValueTextColor(getColor(R.color.pie_dark_green));
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineDataSetExpenses);
        dataSets.add(mLineDataSetIncomes);
        LineData data = new LineData(dataSets);
        data.setValueTextSize(10);

        mLineChart.setData(data);
        double totalExpenses = mBudget.getTotalExpenses();
        double totalIncomes = mBudget.getTotalIncomes();
        if (totalExpenses == 0 && totalIncomes == 0) {
            mLineChart.setVisibility(View.INVISIBLE);
        }
        mLineChart.setNoDataText(resources.getString(R.string.no_data_chart));
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setPinchZoom(true);
        mLineChart.setAutoScaleMinMaxEnabled(true);

        customizeAxis();
        customizeLegend();
        if (!large) {
            mLineChart.getLegend().setEnabled(false);
            setSmallChart(data);
        }

        mLineChart.invalidate(); // refresh
    }

    public void setSmallChart(LineData data) {
        data.setValueTextSize(0);
        data.setDrawValues(false);
        data.setHighlightEnabled(false);

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
        return ContextCompat.getColor(StaticContext.mContext, color);
    }
}
