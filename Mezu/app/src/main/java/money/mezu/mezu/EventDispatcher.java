package money.mezu.mezu;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JB on 5/10/17.
 */

public class EventDispatcher
{
    private HashSet<BudgetUpdatedListener> mBudgetUpdatedListeners;
    private HashSet<ExpenseUpdatedListener> mExpenseUpdatedListeners;
    private static EventDispatcher mInstance = null;
    private EventDispatcher()
    {
        mBudgetUpdatedListeners = new HashSet<BudgetUpdatedListener>();
        mExpenseUpdatedListeners= new HashSet<ExpenseUpdatedListener>();
    }
    //************************************************************************************************************************************************

    public static EventDispatcher getInstance()
    {
        if (null == mInstance)
        {
            mInstance = new EventDispatcher();
        }
        return mInstance;
    }
    //************************************************************************************************************************************************
    public void registerBudgetUpdateListener(BudgetUpdatedListener newListener)
    {
        mBudgetUpdatedListeners.add(newListener);
    }
    //************************************************************************************************************************************************
    public void registerExpenseUpdateListener(ExpenseUpdatedListener newListener)
    {
        mExpenseUpdatedListeners.add(newListener);
    }
    //************************************************************************************************************************************************
    public void notifyBudgetUpdatedListeners(Budget newBudget)
    {
        for (BudgetUpdatedListener listener : mBudgetUpdatedListeners)
        {
            listener.budgetUpdatedCallback(newBudget);
        }
    }
    //************************************************************************************************************************************************
    public void notifyExpenceUpdatedListeners()
    {
        for (ExpenseUpdatedListener listener : mExpenseUpdatedListeners)
        {
            listener.expenseUpdatedCallback();
        }
    }

}
