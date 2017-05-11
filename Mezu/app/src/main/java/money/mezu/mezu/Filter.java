package money.mezu.mezu;

import java.util.ArrayList;
import java.util.Date;

public class Filter {

    static public ArrayList<Expense> filterExpensesByDate
            (ArrayList<Expense> expenses, Date startDate, Date endDate) {
        ArrayList<Expense> filteredExpenses = new ArrayList<Expense>();
        for (Expense expense : expenses){
            Date expenseDate = expense.getTime();
            if (expenseDate.after(startDate) && expenseDate.before(endDate)){
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }

    static public ArrayList<Expense> filterExpensesByCategory
            (ArrayList<Expense> expenses, Category category) {
        ArrayList<Expense> filteredExpenses = new ArrayList<Expense>();
        for (Expense expense : expenses){
            Category expenseCategory = expense.getCategory();
            if (expenseCategory == category){
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }

    static public ArrayList<Expense> filterExpensesByDateAndCategory
            (ArrayList<Expense> expenses, Date startDate, Date endDate, Category category) {
        return filterExpensesByCategory(filterExpensesByDate(expenses,startDate,endDate), category);
    }

}
