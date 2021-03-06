package money.mezu.mezu;

import android.util.Log;

import java.util.Set;

public class BudgetsDownloadedNotifier implements BudgetUpdatedListener {
    private Set<String> bidsToListen;
    private static boolean firstExecution = true;
    private static BudgetsDownloadedNotifier mBudgetsDownloadedNotifier;

    //************************************************************************************************************************************************
    public static void handleIfFirstExecution(Set<String> bids) {
        if (firstExecution) {
            mBudgetsDownloadedNotifier = new BudgetsDownloadedNotifier(bids);
        }
        firstExecution = false;
    }

    //************************************************************************************************************************************************
    private BudgetsDownloadedNotifier(Set<String> bids) {
        bidsToListen = bids;
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
    }

    //************************************************************************************************************************************************
    public static void reset() {
        EventDispatcher.getInstance().unregisterBudgetUpdatedListener(mBudgetsDownloadedNotifier);
        firstExecution = true;
    }

    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget newBudget) {
        if (bidsToListen.contains(newBudget.getId())) {
            bidsToListen.remove(newBudget.getId());
        }
        if (0 == bidsToListen.size()) {
            Log.d("", "BudgetsDownloadedNotifier:budgetUpdatedCallback: notifying that budgets are ready!");
            EventDispatcher.getInstance().notifyLocalCacheReady();
            EventDispatcher.getInstance().unregisterBudgetUpdatedListener(this);
        }
    }

}
