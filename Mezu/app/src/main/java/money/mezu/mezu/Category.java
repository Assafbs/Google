package money.mezu.mezu;

/**
 * Created by asafb on 4/15/2017.
 */

public enum Category {
    FOOD(0),
    SHELTER(1),
    ENTERTAINMENT(2),
    CLOTHING(3),
    TRANSPORTATION(4),
    MEDICAL(5),
    INSURANCE(6),
    HOUSEHOLD_SUPPLIES(7),
    PERSONAL(8),
    EDUCATION(9),
    GIFTS(10),
    SUBSCRIPTIONS(11),
    DEBT_REDUCTION(12),
    DONATIONS(13);

    private final int value;
    private Category(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
