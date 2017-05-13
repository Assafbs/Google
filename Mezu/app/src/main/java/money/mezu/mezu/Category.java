package money.mezu.mezu;

import android.content.res.Resources;

import java.util.ArrayList;

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
    PAYCHECK(14),
    OTHER(15);


    private final int value;
    private Category(int value) {
        this.value = value;
    }


    public static Category getCategoryFromString(String category) {
        Resources resources = staticContext.mContext.getResources();

        if (category.equals(resources.getString(R.string.category_food)))
            return Category.FOOD;
        if (category.equals(resources.getString(R.string.category_clothing)))
            return Category.CLOTHING;
        if (category.equals(resources.getString(R.string.category_entertainment)))
            return Category.ENTERTAINMENT;
        if (category.equals(resources.getString(R.string.category_shelter)))
            return Category.SHELTER;
        if (category.equals(resources.getString(R.string.category_transportation)))
            return Category.TRANSPORTATION;
        if (category.equals(resources.getString(R.string.category_medical)))
            return Category.MEDICAL;
        if (category.equals(resources.getString(R.string.category_household)))
            return Category.HOUSEHOLD_SUPPLIES;
        if (category.equals(resources.getString(R.string.category_insurance)))
            return Category.INSURANCE;
        if (category.equals(resources.getString(R.string.category_personal)))
            return Category.PERSONAL;
        if (category.equals(resources.getString(R.string.category_education)))
            return Category.EDUCATION;
        if (category.equals(resources.getString(R.string.category_gifts)))
            return Category.GIFTS;
        if (category.equals(resources.getString(R.string.category_subscriptions)))
            return Category.SUBSCRIPTIONS;
        if (category.equals(resources.getString(R.string.category_debt_reduction)))
            return Category.DEBT_REDUCTION;
        if (category.equals(resources.getString(R.string.category_donations)))
            return Category.DONATIONS;
        if (category.equals(resources.getString(R.string.category_paycheck)))
            return Category.PAYCHECK;
        if (category.equals(resources.getString(R.string.category_other)))
            return Category.OTHER;

        return null;
    }

    public String toString(){
        Resources resources = staticContext.mContext.getResources();
        switch(this){
            case  FOOD:
                return resources.getString(R.string.category_food);
            case  SHELTER:
                return resources.getString(R.string.category_shelter);
            case  ENTERTAINMENT:
                return resources.getString(R.string.category_entertainment);
            case  EDUCATION:
                return resources.getString(R.string.category_education);
            case  TRANSPORTATION:
                return resources.getString(R.string.category_transportation);
            case  MEDICAL:
                return resources.getString(R.string.category_medical);
            case  INSURANCE:
                return resources.getString(R.string.category_insurance);
            case  HOUSEHOLD_SUPPLIES:
                return resources.getString(R.string.category_household);
            case  PERSONAL:
                return resources.getString(R.string.category_personal);
            case  CLOTHING:
                return resources.getString(R.string.category_clothing);
            case  GIFTS:
                return resources.getString(R.string.category_gifts);
            case  SUBSCRIPTIONS:
                return resources.getString(R.string.category_subscriptions);
            case  DEBT_REDUCTION:
                return resources.getString(R.string.category_debt_reduction);
            case  DONATIONS:
                return resources.getString(R.string.category_donations);
            case  PAYCHECK:
                return resources.getString(R.string.category_paycheck);
            case  OTHER:
                return resources.getString(R.string.category_other);

        }

        return resources.getString(R.string.category_other);
    }

    public int getValue() {
        return value;
    }

}
