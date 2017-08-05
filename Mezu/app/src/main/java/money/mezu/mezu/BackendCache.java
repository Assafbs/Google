package money.mezu.mezu;

import android.util.Log;

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
    public void clearCache()
    {
        mCache = null;
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget newBudget)
    {
        Log.d("",String.format("BackendCache:budgetUpdatedCallback: invoked with budget: %s", newBudget.toString()));
        budgets.put(newBudget.getId(), newBudget);
    }
    //************************************************************************************************************************************************
    public void userLeftBudgetCallback(String bid)
    {
        Log.d("",String.format("BackendCache:userLeftBudgetCallback: invoked with bid: %s", bid));
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
