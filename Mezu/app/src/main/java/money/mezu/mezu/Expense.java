package money.mezu.mezu;

import java.sql.Time;

/**
 * Created by asafb on 4/15/2017.
 */

public class Expense {

    private ExpenseIdentifier id;
    private double amount; // Temporary, should probably be changed
    private String description;
    private Category category;
    private Time time;

    public Expense(ExpenseIdentifier id, double amount, String description, Category category, Time time) {
        super();
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.time = time;
    }

    public ExpenseIdentifier getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Time getTime() {
        return time;
    }
}

