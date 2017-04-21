package money.mezu.mezu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asafb on 4/15/2017.
 */

public class Budget {

    private BudgetIdentifier id;
    private List<Expense> expenses;
    private String name;

    public Budget(BudgetIdentifier id, String name) {
        super();
        this.id = id;
        this.name = name;
        expenses = new ArrayList<>();
        //TODO: backend to fill
    }

    public BudgetIdentifier getId() {
        return id;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public String toString(){ return name; }
}
