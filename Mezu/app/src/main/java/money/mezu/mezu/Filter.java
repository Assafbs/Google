package money.mezu.mezu;

import java.util.ArrayList;
import java.util.Date;

public class Filter {

    static public ArrayList<Expense> filterExpensesByDate
            (ArrayList<Expense> expenses, Date startDate, Date endDate) {
        ArrayList<Expense> filteredExpenses = new ArrayList<Expense>();
        for (Expense expense : expenses) {
            Date expenseDate = expense.getTime();
            if (expenseDate.after(startDate) && expenseDate.before(endDate)) {
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }
}
