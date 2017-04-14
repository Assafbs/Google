package money.mezu.mezu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asafb on 4/15/2017.
 */

public class Budget {

    private BudgetIdentifier id;
    private List<Expense> expenses;

    public Budget(BudgetIdentifier id) {
        super();
        this.id = id;
        expenses = new ArrayList<>();
        //TODO: backend to fill
    }

    public BudgetIdentifier getId() {
        return id;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}
