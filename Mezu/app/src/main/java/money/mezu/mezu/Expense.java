package money.mezu.mezu;

import java.math.BigInteger;
import java.sql.Time;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by asafb on 4/15/2017.
 */

public class Expense {

    private ExpenseIdentifier id;
    private double amount; // Temporary, should probably be changed
    private String description;
    private Category category;
    private Time time;
    private UserIdentifier userID;
    private String userName;

    public Expense(ExpenseIdentifier id, double amount, String description, Category category, Time time) {
        super();
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.time = time;
        this.userID = new UserIdentifier(new BigInteger("1234"));
        this.userName = "Assafim";
    }

    public Expense(HashMap<String, Object> serializedExpense)
    {
        super();
        this.id = new ExpenseIdentifier((new BigInteger((String) serializedExpense.get("id"))));
        this.amount = (double)serializedExpense.get("amount");
        this.description = (String) serializedExpense.get("description");
        this.category = Category.values()[(int)serializedExpense.get("category")];
        this.time = new Time((long) serializedExpense.get("time"));
        this.userID =  new UserIdentifier((new BigInteger((String) serializedExpense.get("userID"))));
        this.userName = (String) serializedExpense.get("userName");
    }

    public Expense(ExpenseIdentifier id, double amount, Category category) {
        super();
        this.id = id;
        this.amount = amount;
        this.category = category;
        //TODO: use SessionManager to get info on user
        this.userID = new UserIdentifier(new BigInteger("1234"));
        this.userName = "Assafim";
    }

    public Expense(ExpenseIdentifier id, double amount, Category category, String userName) {
        super();
        this.id = id;
        this.amount = amount;
        this.category = category;
        //TODO: use SessionManager to get info on user
        this.userID = new UserIdentifier(new BigInteger("1234"));
        this.userName = userName;
    }
    public HashMap<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("id", id.getId().toString());
        serialized.put("amount", amount);
        serialized.put("description", description);
        serialized.put("category", category.getValue());
        serialized.put("time", time.getTime());
        serialized.put("userID", userID.getId().toString());
        serialized.put("userName", userName);
        return serialized;
    }

    public BigInteger getId() {
        return id.getId();
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

    public UserIdentifier getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }
}

