package money.mezu.mezu;

import android.content.res.Resources;

import java.util.ArrayList;

public enum Category {
    CATEGORY(0, true, true),
    FOOD(1, true, false),
    SHELTER(2, true, false),
    ENTERTAINMENT(3, true, false),
    CLOTHING(4, true, false),
    TRANSPORTATION(5, true, false),
    MEDICAL(6, true, false),
    INSURANCE(7, true, false),
    HOUSEHOLD_SUPPLIES(8, true, false),
    PERSONAL(9, true, false),
    EDUCATION(10, true, false),
    GIFTS(11, true, true),
    SUBSCRIPTIONS(12, true, false),
    DEBT_REDUCTION(13, true, false),
    DONATIONS(14, true, false),
    PAYCHECK(15, false, true),
    OTHER(16, true, true);


    static ArrayList<String> incomeCats = null;
    static ArrayList<String> expenseCats = null;

    private final int value;
    private boolean isExpense;
    private boolean isIncome;

    private Category(int value, boolean isExpense, boolean isIncome) {
        this.value = value;
        this.isExpense = isExpense;
        this.isIncome = isIncome;
    }


    public static Category getCategoryFromString(String category) {
        Resources resources = StaticContext.mContext.getResources();
        if (category.contains("\t")) {
            category = category.split("\t")[1];
        }

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

        return CATEGORY;
    }

    public String toString() {
        Resources resources = StaticContext.mContext.getResources();
        switch (this) {
            case CATEGORY:
                return resources.getString(R.string.pick_a_category);
            case FOOD:
                return resources.getString(R.string.category_food);
            case SHELTER:
                return resources.getString(R.string.category_shelter);
            case ENTERTAINMENT:
                return resources.getString(R.string.category_entertainment);
            case EDUCATION:
                return resources.getString(R.string.category_education);
            case TRANSPORTATION:
                return resources.getString(R.string.category_transportation);
            case MEDICAL:
                return resources.getString(R.string.category_medical);
            case INSURANCE:
                return resources.getString(R.string.category_insurance);
            case HOUSEHOLD_SUPPLIES:
                return resources.getString(R.string.category_household);
            case PERSONAL:
                return resources.getString(R.string.category_personal);
            case CLOTHING:
                return resources.getString(R.string.category_clothing);
            case GIFTS:
                return resources.getString(R.string.category_gifts);
            case SUBSCRIPTIONS:
                return resources.getString(R.string.category_subscriptions);
            case DEBT_REDUCTION:
                return resources.getString(R.string.category_debt_reduction);
            case DONATIONS:
                return resources.getString(R.string.category_donations);
            case PAYCHECK:
                return resources.getString(R.string.category_paycheck);
            case OTHER:
                return resources.getString(R.string.category_other);

        }

        return resources.getString(R.string.category_other);
    }

    public String getEmojiWithName() {
        Resources resources = StaticContext.mContext.getResources();
        LanguageUtils mLU = new LanguageUtils();
        switch (this) {
            case CATEGORY:
                return resources.getString(R.string.pick_a_category);
            case FOOD:
                return mLU.getEmojiByUnicode(0x1F355) + "\t" + this;
            case SHELTER:
                return mLU.getEmojiByUnicode(0x1F3E0) + "\t" + this;
            case ENTERTAINMENT:
                return mLU.getEmojiByUnicode(0x1F3AE) + "\t" + this;
            case EDUCATION:
                return mLU.getEmojiByUnicode(0x1F4DA) + "\t" + this;
            case TRANSPORTATION:
                return mLU.getEmojiByUnicode(0x1F68E) + "\t" + this;
            case MEDICAL:
                return mLU.getEmojiByUnicode(0x1F489) + "\t" + this;
            case INSURANCE:
                return mLU.getEmojiByUnicode(0x1F4BC) + "\t" + this;
            case HOUSEHOLD_SUPPLIES:
                return mLU.getEmojiByUnicode(0x1F6AA) + "\t" + this;
            case PERSONAL:
                return mLU.getEmojiByUnicode(0x1F64A) + "\t" + this;
            case CLOTHING:
                return mLU.getEmojiByUnicode(0x1F454) + "\t" + this;
            case GIFTS:
                return mLU.getEmojiByUnicode(0x1F381) + "\t" + this;
            case SUBSCRIPTIONS:
                return mLU.getEmojiByUnicode(0x1F4DC) + "\t" + this;
            case DEBT_REDUCTION:
                return mLU.getEmojiByUnicode(0x1F4B0) + "\t" + this;
            case DONATIONS:
                return mLU.getEmojiByUnicode(0x1F607) + "\t" + this;
            case PAYCHECK:
                return mLU.getEmojiByUnicode(0x2709) + "\t" + this;
            case OTHER:
                return resources.getString(R.string.category_other);
        }

        return resources.getString(R.string.category_other);
    }

    public int getValue() {
        return value;
    }

    public int getSpinnerLocation(boolean isIncome) {
        int loc = 0;
        for (Category cat : Category.values()) {
            if (isIncome ? cat.isIncome : cat.isExpense) {
                if (this == cat)
                    return loc;
                else
                    loc++;
            }
        }
        throw new IllegalArgumentException();
    }

    public static ArrayList<String> getIncomeCategoriesList() {
        if (incomeCats == null) {
            incomeCats = new ArrayList<>();

            for (Category cat : Category.values()) {
                if (cat.isIncome) {
                    incomeCats.add(cat.getEmojiWithName());
                }
            }
        }
        return incomeCats;
    }

    public static ArrayList<String> getExpenseCategoriesList() {
        if (expenseCats == null) {
            expenseCats = new ArrayList<>();

            for (Category cat : Category.values()) {
                if (cat.isExpense) {
                    expenseCats.add(cat.getEmojiWithName());
                }
            }
        }
        return expenseCats;
    }
}
