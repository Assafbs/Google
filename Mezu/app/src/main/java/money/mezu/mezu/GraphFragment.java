package money.mezu.mezu;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

public class GraphFragment extends Fragment {
    private View mView;

    private PieChart mPieChart;
    private BarChart mBarChart;
    private PieChartCategories mPieChartCategories;

    private GraphInterface mGraph;

    private BudgetViewActivity mActivity;

    public void setupPieChart (GraphInterface graph, PieChart pieChart, PieChartCategories pieChartCategories) {
        this.mGraph = graph;
        this.mPieChart = pieChart;
        this.mPieChartCategories = pieChartCategories;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //LinearLayout viewGroup = null;

        mActivity = (BudgetViewActivity) getActivity();

        if (mGraph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            mView = inflater.inflate(R.layout.pie_chart_view, null);
            //viewGroup = (LinearLayout) mView.findViewById(R.id.pie_chart_view);
        }

        TextView titleView = (TextView) mView.findViewById(R.id.pie_chart_title);
        titleView.setText(mGraph.getTitle());

        if (mGraph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            mPieChart = (PieChart) mView.findViewById(R.id.pie_chart);
            mPieChartCategories.setPieChart(mPieChart);
            mPieChartCategories.GenerateGraph(mView, mActivity.mCurrentBudget, true);
        }

        return mView;
    }
}
