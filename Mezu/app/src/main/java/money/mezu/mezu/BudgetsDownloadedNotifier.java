package money.mezu.mezu;

import android.util.Log;

import java.util.Set;

/**
 * Created by JB on 6/10/17.
 */

public class BudgetsDownloadedNotifier implements  BudgetUpdatedListener{
    Set<String> bidsToListen;
    static boolean firstExecution = true;
    //************************************************************************************************************************************************
    public static void handleIfFirstExecution(Set<String> bids)
    {
        if(firstExecution )
        {
            new BudgetsDownloadedNotifier(bids);
        }
        firstExecution = false;
    }
    //************************************************************************************************************************************************
    private BudgetsDownloadedNotifier(Set<String> bids)
    {
        bidsToListen = bids;
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget newBudget)
    {
        if(bidsToListen.contains(newBudget.getId()))
        {
            bidsToListen.remove(newBudget.getId());
        }
        if (0 == bidsToListen.size())
        {
            Log.d("","BudgetsDownloadedNotifier:budgetUpdatedCallback: notifying that budgets are ready!");
            EventDispatcher.getInstance().notifyLocalCacheReady();
            EventDispatcher.getInstance().unregisterBudgetUpdatedListener(this);
        }
    }
}
