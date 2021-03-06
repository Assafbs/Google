package money.mezu.mezu;

import java.util.Comparator;

public class DateAndTimeComparator implements Comparator<Expense> {

    @Override
    public int compare(Expense e1, Expense e2) {
        return -1 * e1.getTime().compareTo(e2.getTime());
    }
}
