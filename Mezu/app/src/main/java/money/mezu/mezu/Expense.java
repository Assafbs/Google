package money.mezu.mezu;

import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by asafb on 4/15/2017.
 */

public class Expense {

    private String mId;
    private double mAmount; // Temporary, should probably be changed
    private String mDescription;
    private Category mCategory;
    private Date mTime;
    private UserIdentifier mUserID;
    private String mUserName;

    public Expense(String id, double amount, String description, Category category, Date time) {
        super();
        this.mId = id;
        this.mAmount = amount;
        this.mDescription = description;
        this.mCategory = category;
        this.mTime = time;
        this.mUserID = new UserIdentifier(new BigInteger("1234"));
        this.mUserName = "Assafim";
    }

    public Expense(HashMap<String, Object> serializedExpense)
    {
        super();
        this.mId = (String) serializedExpense.get("mId");
        this.mAmount = (double)serializedExpense.get("mAmount");
        this.mDescription = (String) serializedExpense.get("mDescription");
        this.mCategory = Category.values()[(int)serializedExpense.get("mCategory")];
        this.mTime = new Date((long) serializedExpense.get("mTime"));
        this.mUserID =  new UserIdentifier((new BigInteger((String) serializedExpense.get("mUserID"))));
        this.mUserName = (String) serializedExpense.get("mUserName");
    }

    public Expense(String id, double amount, Category category) {
        super();
        this.mId = id;
        this.mAmount = amount;
        this.mCategory = category;
        //TODO: use SessionManager to get info on user
        this.mUserID = new UserIdentifier(new BigInteger("1234"));
        this.mUserName = "Assafim";
    }

    public Expense(String id, double amount, Category category, String userName) {
        super();
        this.mId = id;
        this.mAmount = amount;
        this.mCategory = category;
        //TODO: use SessionManager to get info on user
        this.mUserID = new UserIdentifier(new BigInteger("1234"));
        this.mUserName = userName;
    }
    public HashMap<String, Object> serialize()
    {
        HashMap<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("mId", mId);
        serialized.put("mAmount", mAmount);
        serialized.put("mDescription", mDescription);
        serialized.put("mCategory", mCategory.getValue());
        serialized.put("mTime", mTime.getTime());
        serialized.put("mUserID", mUserID.getId().toString());
        serialized.put("mUserName", mUserName);
        return serialized;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) { mId = id; }

    public Category getCategory() {
        return mCategory;
    }

    public double getAmount() {
        return mAmount;
    }

    public String getDescription() {
        return mDescription;
    }

    public Date getTime() {
        return mTime;
    }

    public UserIdentifier getUserID() {
        return mUserID;
    }

    public String getUserName() {
        return mUserName;
    }
}

