package money.mezu.mezu;

public enum GraphEnum {
    PIE_CHART(0),
    BAR_CHART(1),
    LINE_CHART(2);

    private final int value;
    private GraphEnum(int value) {
        this.value = value;
    }
}
