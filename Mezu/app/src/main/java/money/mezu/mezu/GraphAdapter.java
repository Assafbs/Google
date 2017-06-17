package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

public class GraphAdapter extends ArrayAdapter<GraphInterface> {
    private Context mContext;
    private PieChart mPieChart;
    private BarChart mBarChart;
    private LineChart mLineChart;
    private PieChartCategories mPieChartCategories;
    private LineChartMonths mLineChartMonths;
    private BarChartUsers mBarChartUsers;
    private Resources resources = StaticContext.mContext.getResources();
    private BudgetViewActivity mActivity;

    public GraphAdapter(Context context, ArrayList<GraphInterface> graphs) {
        super(context, 0, graphs);
        mContext = context;
        mActivity = (BudgetViewActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GraphInterface graph = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_graph, parent, false);
        }

        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.graphTitle);
        TextView info1 = (TextView) convertView.findViewById(R.id.graphInfo1);
        TextView info2 = (TextView) convertView.findViewById(R.id.graphInfo2);
        TextView info3 = (TextView) convertView.findViewById(R.id.graphInfo3);
        mPieChart = (PieChart) convertView.findViewById(R.id.pie_chart_small);
        mBarChart = (BarChart) convertView.findViewById(R.id.bar_chart_small);
        mLineChart = (LineChart) convertView.findViewById(R.id.line_chart_small);
        info2.setText(graph.getInfoLine());
        info3.setText(graph.getInfoValue());
        if (LanguageUtils.isRTL() &&
                graph.getTitle().equals(resources.getString(R.string.expenses_by_users)) &&
                mActivity.mCurrentBudget.getTotalExpenses() != 0) {
            info3.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

        LinearLayout graphRow = (LinearLayout) convertView.findViewById(R.id.graphRow);
        graphRow.setTag(graph);

        if (graph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            handlePieChart(convertView, graph);
        } else if (graph.getGraphKind().equals(GraphEnum.LINE_CHART)) {
            handleLineChart(convertView, graph);
        } else if (graph.getGraphKind().equals(GraphEnum.BAR_CHART)) {
            handleBarChart(convertView, graph);
        }
        title.setText(graph.getTitle());

        graphRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GraphInterface graph = (GraphInterface) view.getTag();
                replaceTab(graph);
                ((Activity) mContext).findViewById(R.id.fab_expense).setVisibility(View.INVISIBLE);
            }
        });

        if (mActivity.mCurrentBudget.getTotalExpenses() == 0) {
            if (!graph.getTitle().equals(resources.getString(R.string.expenses_by_months)) || mActivity.mCurrentBudget.getTotalIncomes() == 0) {
                graphRow.setClickable(false);
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

    void handlePieChart(View convertView, GraphInterface graph) {
        ViewGroup.LayoutParams barParams = mBarChart.getLayoutParams();
        ViewGroup.LayoutParams lineParams = mLineChart.getLayoutParams();
        barParams.width = 0;
        lineParams.width = 0;
        mBarChart.setLayoutParams(barParams);
        mLineChart.setLayoutParams(lineParams);
        mPieChartCategories = (PieChartCategories) graph;
        mPieChartCategories.setPieChart(mPieChart);
        mPieChartCategories.GenerateGraph(convertView, false);
    }

    void handleLineChart(View convertView, GraphInterface graph) {
        ViewGroup.LayoutParams barParams = mBarChart.getLayoutParams();
        ViewGroup.LayoutParams pieParams = mPieChart.getLayoutParams();
        barParams.width = 0;
        pieParams.width = 0;
        mBarChart.setLayoutParams(barParams);
        mPieChart.setLayoutParams(pieParams);
        mLineChartMonths = (LineChartMonths) graph;
        mLineChartMonths.setLineChart(mLineChart);
        mLineChartMonths.GenerateGraph(convertView, false);
    }

    void handleBarChart(View convertView, GraphInterface graph) {
        ViewGroup.LayoutParams lineParams = mLineChart.getLayoutParams();
        ViewGroup.LayoutParams pieParams = mPieChart.getLayoutParams();
        lineParams.width = 0;
        pieParams.width = 0;
        mLineChart.setLayoutParams(lineParams);
        mPieChart.setLayoutParams(pieParams);
        mBarChartUsers = (BarChartUsers) graph;
        mBarChartUsers.setBarChart(mBarChart);
        mBarChartUsers.GenerateGraph(convertView, false);
    }

    private void replaceTab(GraphInterface graph) {
        GraphFragment graphFragment = new GraphFragment();
        if (graph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            graphFragment.setupPieChart(graph, mPieChart, mPieChartCategories);
        } else if (graph.getGraphKind().equals(GraphEnum.LINE_CHART)) {
            graphFragment.setupLineChart(graph, mLineChart, mLineChartMonths);
        } else if (graph.getGraphKind().equals(GraphEnum.BAR_CHART)) {
            graphFragment.setupBarChart(graph, mBarChart, mBarChartUsers);
        }
        mActivity.mViewPagerAdapter.onSwitchToGraph(graphFragment);
        mActivity.graphShown = true;
    }
}
