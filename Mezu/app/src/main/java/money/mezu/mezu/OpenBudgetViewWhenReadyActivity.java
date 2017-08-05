package money.mezu.mezu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by JB on 7/31/17.
 */

public class OpenBudgetViewWhenReadyActivity extends Activity implements LocalCacheReadyListener
{
    boolean launchedBudgetView = false;
    String mBid = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_budget_view_when_ready);
        StaticContext.mContext = this;
        Log.d("", "OpenBudgetViewWhenReadyActivity::onCreate start");
        Intent startingIntent = getIntent();
        if (startingIntent != null)
        {
            mBid = startingIntent.getStringExtra("bid"); // Retrieve the id
            EventDispatcher.getInstance().registerLocalCacheReadyListener(this);
            Log.d("", String.format("OpenBudgetViewWhenReadyActivity::onCreate bid is $s", mBid));
            if (BackendCache.getInstatnce().getBudgets().containsKey(mBid))
            {
                SessionManager sessionManager = new SessionManager(this);
                BudgetViewActivity.goToBudgetView(this, BackendCache.getInstatnce().getBudgets().get(mBid), sessionManager);
                launchedBudgetView = true;
                finish();
            }
        }
    }

    public void localCacheReadyCallback()
    {
        Log.d("", "OpenBudgetViewWhenReadyActivity::localCacheReadyCallback start");
        if(!launchedBudgetView)
        {
            if (BackendCache.getInstatnce().getBudgets().containsKey(mBid))
            {
                SessionManager sessionManager = new SessionManager(this);
                BudgetViewActivity.goToBudgetView(this, BackendCache.getInstatnce().getBudgets().get(mBid), sessionManager);
            }
            launchedBudgetView = true;
            finish();
        }

    }

}
