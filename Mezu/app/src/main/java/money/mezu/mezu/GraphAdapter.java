package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

public class GraphAdapter extends ArrayAdapter<GraphInterface> {
    private Context mContext;
    private PieChart mPieChart;
    private BarChart mBarChart;
    private PieChartCategories mPieChartCategories;
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

        LinearLayout graphRow = (LinearLayout) convertView.findViewById(R.id.graphRow);
        graphRow.setTag(graph);

        if (graph.getGraphKind().equals(GraphEnum.PIE_CHART)) {
            handlePieChart(convertView, graph, info1, info2, info3);
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

        // Return the completed view to render on screen
        return convertView;
    }

    void handlePieChart(View convertView, GraphInterface graph, TextView info1, TextView info2, TextView info3) {
        ViewGroup.LayoutParams params = mBarChart.getLayoutParams();
        params.width = 0;
        mBarChart.setLayoutParams(params);
        mPieChartCategories = (PieChartCategories) graph;
        mPieChartCategories.setPieChart(mPieChart);
        mPieChartCategories.GenerateGraph(convertView, mActivity.mCurrentBudget, false);
        info2.setText(R.string.most_spending_on);
        info3.setText(mActivity.mCurrentBudget.getMostExpensiveCategory().toString());
    }

    private void replaceTab (GraphInterface graph) {
        GraphFragment graphFragment = new GraphFragment();
        //if (graph.kind == piechart)...
        graphFragment.setupPieChart(graph,mPieChart,mPieChartCategories);
        mActivity.mViewPagerAdapter.onSwitchToGraph(graphFragment);
        mActivity.graphShown = true;
    }
}
