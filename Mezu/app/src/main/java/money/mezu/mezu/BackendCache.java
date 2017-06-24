package money.mezu.mezu;


import java.util.HashMap;

/**
 * Created by JB on 5/23/17.
 */

public class BackendCache implements BudgetUpdatedListener, UserLeftBudgetListener {
    private static BackendCache mCache= null;
    private HashMap<String, Budget> budgets = new HashMap<String, Budget> ();
    //************************************************************************************************************************************************
    public static BackendCache getInstatnce()
    {
        if (null == mCache)
        {
            mCache = new BackendCache();
        }
        return mCache;
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget newBudget)
    {
        budgets.put(newBudget.getId(), newBudget);
    }
    //************************************************************************************************************************************************
    public void userLeftBudgetCallback(String bid)
    {
        budgets.remove(bid);
    }
    //************************************************************************************************************************************************
    private BackendCache()
    {
        FirebaseBackend.getInstance().startListeningForAllUserBudgetUpdates((new SessionManager(StaticContext.mContext)).getUserId());
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
        EventDispatcher.getInstance().registerUserLeftBudgetListener(this);
    }
    //************************************************************************************************************************************************
    public HashMap<String, Budget> getBudgets()
    {
        return budgets;
    }
}
