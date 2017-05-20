package money.mezu.mezu;

/**
 * Created by davidled on 20/05/2017.
 */

public enum GraphEnum {
    PIE_CHART(0),
    BAR_CHART(1);

    private final int value;
    private GraphEnum(int value) {
        this.value = value;
    }
}
