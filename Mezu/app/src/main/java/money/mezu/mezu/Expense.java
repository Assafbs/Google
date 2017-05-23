package money.mezu.mezu;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Expense {

    private String mId;
    private double mAmount; // Temporary, should probably be changed
    private String mDescription;
    private String mTitle;
    private Category mCategory;
    private Date mTime;
    private UserIdentifier mUserID;
    private String mUserName;
    private boolean mIsExpense;

    public Expense(String id,
                   double amount,
                   String mTitle,
                   String description,
                   Category category,
                   Date time,
                   UserIdentifier uid,
                   String userName,
                   boolean isExpense) {
        super();
        this.mId = id;
        this.mAmount = amount;
        this.mTitle = mTitle;
        this.mDescription = description;
        this.mCategory = category;
        this.mTime = time;
        this.mUserID = uid;
        this.mUserName = userName;
        this.mIsExpense = isExpense;
    }

    public Expense(HashMap<String, Object> serializedExpense) {
        super();
        this.mId = (String) serializedExpense.get("mId");
        this.mAmount = Double.parseDouble(serializedExpense.get("mAmount").toString());
        this.mTitle = (String) serializedExpense.get("mTitle");
        this.mDescription = (String) serializedExpense.get("mDescription");
        //CAN BE DELETED BEFORE RELEASE
        try {
            this.mCategory = Category.values()[Integer.parseInt(serializedExpense.get("mCategory").toString())];
        } catch (NumberFormatException e) {
            this.mCategory = (Category) Category.getCategoryFromString(serializedExpense.get("mCategory").toString());
        }

        this.mTime = new Date((long) serializedExpense.get("mTime"));
        this.mUserID = new UserIdentifier((new BigInteger((String) serializedExpense.get("mUserID"))));
        this.mUserName = (String) serializedExpense.get("mUserName");
        //CAN BE DELETED BEFORE RELEASE
        try {
            this.mIsExpense = (boolean) serializedExpense.get("mIsExpense");
        } catch (NullPointerException e) {
            this.mIsExpense = true;
        }
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("mId", mId);
        serialized.put("mAmount", mAmount);
        serialized.put("mTitle", mTitle);
        serialized.put("mDescription", mDescription);
        serialized.put("mCategory", mCategory.getValue());
        serialized.put("mTime", mTime.getTime());
        serialized.put("mUserID", mUserID.getId().toString());
        serialized.put("mUserName", mUserName);
        serialized.put("mIsExpense", mIsExpense);
        return serialized;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Category getCategory() {
        return mCategory;
    }

    public double getAmount() {
        return mAmount;
    }

    public String getTitle() {
        return mTitle;
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

    public boolean getIsExpense() {
        return mIsExpense;
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(mTime);
        return c.get(Calendar.MONTH);
    }

    public int getYear() {
        Calendar c = Calendar.getInstance();
        c.setTime(mTime);
        return c.get(Calendar.YEAR);
    }
}

