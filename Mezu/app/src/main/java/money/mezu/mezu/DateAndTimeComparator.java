package money.mezu.mezu;

import java.util.Comparator;

/**
 * Created by David on 15/8/2017.
 */

public class DateAndTimeComparator implements Comparator<Expense> {

    @Override
    public int compare(Expense e1, Expense e2) {
        return e1.getTime().compareTo(e2.getTime());
    }
}
