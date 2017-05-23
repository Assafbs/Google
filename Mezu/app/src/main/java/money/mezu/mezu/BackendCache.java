package money.mezu.mezu;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JB on 5/23/17.
 */

public class BackendCache implements BudgetUpdatedListener {
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
    private BackendCache()
    {
        FirebaseBackend.getInstance().startListeningForAllUserBudgetUpdates((new SessionManager(staticContext.mContext)).getUserId());
    }
    //************************************************************************************************************************************************
    public HashMap<String, Budget> getBudgets()
    {
        return budgets;
    }
}
