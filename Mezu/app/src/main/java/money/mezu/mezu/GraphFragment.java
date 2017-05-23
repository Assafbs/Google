package money.mezu.mezu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

public class GraphFragment extends Fragment {
    private View mView;

    private PieChart mPieChart;
    private BarChart mBarChart;
    private LineChart mLineChart;
    private LineChartMonths mLineChartMonths;
    private PieChartCategories mPieChartCategories;

    private GraphInterface mGraph;

    private BudgetViewActivity mActivity;

    public void setupPieChart(GraphInterface graph, PieChart pieChart, PieChartCategories pieChartCategories) {
        this.mGraph = graph;
        this.mPieChart = pieChart;
        this.mPieChartCategories = pieChartCategories;
    }

    public void setupLineChart(GraphInterface graph, LineChart lineChart, LineChartMonths lineChartMonths) {
        mGraph = graph;
        mLineChart = lineChart;
        mLineChartMonths = lineChartMonths;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (BudgetViewActivity) getActivity();
        if (mGraph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            mView = inflater.inflate(R.layout.pie_chart_view, null);
        } else if (mGraph.getGraphKind().equals(GraphEnum.LINE_CHART)) {
            mView = inflater.inflate(R.layout.line_chart_view, null);
        }

        TextView titleView = (TextView) mView.findViewById(R.id.chart_title);
        titleView.setText(mGraph.getTitle());

        if (mGraph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            mPieChart = (PieChart) mView.findViewById(R.id.pie_chart);
            mPieChartCategories.setPieChart(mPieChart);
            mPieChartCategories.GenerateGraph(mView, mActivity.mCurrentBudget, true);
        } else if (mGraph.getGraphKind().equals(GraphEnum.LINE_CHART)) {
            mLineChart = (LineChart) mView.findViewById(R.id.line_chart);
            mLineChartMonths.setLineChart(mLineChart);
            mLineChartMonths.GenerateGraph(mView, mActivity.mCurrentBudget, true);
        }

        return mView;
    }

}
