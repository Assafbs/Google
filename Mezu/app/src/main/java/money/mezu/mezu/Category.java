package money.mezu.mezu;

import android.content.res.Resources;

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
    DONATIONS(13),
    PAYCHECK(14);

    private final int value;
    private Category(int value) {
        this.value = value;
    }
    public static Category getCategoryFromString(String category)
    {
        Resources resources = staticContext.mContext.getResources();

        if (category == resources.getString(R.string.category_food))
                return Category.FOOD;
        if (category == resources.getString(R.string.category_clothing))
                return Category.CLOTHING;
        if (category == resources.getString(R.string.category_entertainment))
                return Category.ENTERTAINMENT;
        if (category == resources.getString(R.string.category_shelter))
                return Category.SHELTER;
        if (category == resources.getString(R.string.category_transportation))
                return Category.TRANSPORTATION;
        if (category == resources.getString(R.string.category_medical))
                return Category.MEDICAL;
        if (category == resources.getString(R.string.category_household))
                return Category.HOUSEHOLD_SUPPLIES;
        if (category == resources.getString(R.string.category_insurance))
                return Category.INSURANCE;
        if (category == resources.getString(R.string.category_personal))
                return Category.PERSONAL;
        if (category == resources.getString(R.string.category_education))
                return Category.EDUCATION;
        if (category == resources.getString(R.string.category_gifts))
                return Category.GIFTS;
        if (category == resources.getString(R.string.category_subscriptions))
                return Category.SUBSCRIPTIONS;
        if (category == resources.getString(R.string.category_debt_reduction))
                return Category.DEBT_REDUCTION;
        if (category == resources.getString(R.string.category_donations))
                return Category.DONATIONS;
        if (category == resources.getString(R.string.category_paycheck))
                return Category.PAYCHECK;

        return null;

//        switch (category)
//        {
//            case "Food":
//                return Category.FOOD;
//            case "Shelter":
//                return Category.SHELTER;
//            case "Entertainment":
//                return Category.ENTERTAINMENT;
//            case "Clothing":
//                return Category.CLOTHING;
//            case "Transportation":
//                return Category.TRANSPORTATION;
//            case "Medical":
//                return Category.MEDICAL;
//            case "Household Supplies":
//                return Category.HOUSEHOLD_SUPPLIES;
//            case "Insurance":
//                return Category.INSURANCE;
//            case "Personal":
//                return Category.PERSONAL;
//            case "Education":
//                return Category.EDUCATION;
//            case "Gifts":
//                return Category.GIFTS;
//            case "Subscriptions":
//                return Category.SUBSCRIPTIONS;
//            case "Debt Reduction":
//                return Category.DEBT_REDUCTION;
//            case "Donations":
//                return Category.DONATIONS;
//            case "Paycheck":
//                return Category.PAYCHECK;
//            default:
//                return null;
//        }
    }

    public int getValue() {
        return value;
    }

}
