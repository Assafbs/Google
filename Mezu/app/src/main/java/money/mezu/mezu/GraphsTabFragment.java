package money.mezu.mezu;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class GraphsTabFragment extends Fragment {

    private View mView = null;
    private GraphAdapter mGraphAdapter = null;
    private Resources resources = staticContext.mContext.getResources();

    private BudgetViewActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_graphs, container, false);
        mActivity = (BudgetViewActivity) getActivity();
        // Create the adapter to convert the array to views
        mGraphAdapter = new GraphAdapter(getActivity(), getArrayOfGraphs());
        // Attach the adapter to a ListView
        ListView listView = (ListView) mView.findViewById(R.id.graphs_list);
        listView.setAdapter(mGraphAdapter);
        return mView;
    }

    public ArrayList<GraphInterface> getArrayOfGraphs() {
        ArrayList<GraphInterface> graphArray = new ArrayList<>();
        PieChartCategories pie = new PieChartCategories(resources.getString(R.string.expenses_per_categories));

        //ADD GRAPHS HERE! ONE PER EACH CLASS CREATED
        graphArray.add(pie);
        return graphArray;
    }

}
