package money.mezu.mezu;

import android.view.View;

public interface GraphInterface {
    void calculateDataSet(Budget budget);

    void customizeLegend();

    String getTitle();

    void GenerateGraph(View view, Budget budget, boolean large);

    GraphEnum getGraphKind();
}
