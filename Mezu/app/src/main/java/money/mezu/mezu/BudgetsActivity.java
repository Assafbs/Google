package money.mezu.mezu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class BudgetsActivity extends BaseNavDrawerActivity {

    private HashMap<String, Budget> mapOfBudgets = new HashMap<String, Budget> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        staticContext.mContext = getApplicationContext();
        // This code will make the app go to the login screen if the user is not connected
        mSessionManager = new SessionManager(this);
        if(!mSessionManager.checkLogin()) {
            return;
        }
        UserIdentifier uid = mSessionManager.getUserId();

        setContentView(R.layout.activity_budgets);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBudgetIntent = new Intent(BudgetsActivity.this, AddBudgetActivity.class);
                startActivity(addBudgetIntent);
            }
        });

        mBackend.registerForAllUserBudgetUpdates(this, uid);
    }
    //************************************************************************************************************************************************
    public void updateBudgetsCallback(Budget budget)
    {
        Log.d("",String.format("BudgetsActivity:updateBudgetsCallback: invoked with budget: %s", budget.toString()));
        this.mapOfBudgets.put(budget.getId(), budget);
        ListView listView = (ListView) findViewById(R.id.budgets_list);
        BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
        listView.setAdapter(adapter);
    }
    //************************************************************************************************************************************************
    public Budget getBudgetByID(String id)
    {
        return this.mapOfBudgets.get(id);
    }

}
