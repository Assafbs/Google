package money.mezu.mezu;

import android.view.View;

public interface GraphInterface {
    void calculateDataSet();

    void customizeLegend();

    String getTitle();

    String getInfoLine();

    String getInfoValue();

    void GenerateGraph(View view, boolean large);

    GraphEnum getGraphKind();
}
