package money.mezu.mezu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class BudgetsActivity extends BaseNavDrawerActivity implements  BudgetUpdatedListener{

    private HashMap<String, Budget> mapOfBudgets = new HashMap<String, Budget> ();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setLanguage();
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
        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
        mBackend.startListeningForAllUserBudgetUpdates(uid);
    }

    private void setLanguage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = sharedPref.getString("language", "");
        if (!language.isEmpty()){
            if (!Locale.getDefault().getISO3Language().equals(language)){
                String languageCode;
                if (language.equals("heb")){
                    languageCode = "he";
                }
                else {
                    languageCode = "en";
                }
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                android.content.res.Configuration conf = res.getConfiguration();
                conf.setLocale(new Locale(languageCode.toLowerCase()));
                res.updateConfiguration(conf, dm);
            }
        }
    }

    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget budget)
    {
        Log.d("",String.format("BudgetsActivity:updateBudgetsCallback: invoked with budget: %s", budget.toString()));
        if (mapOfBudgets.containsKey(budget.getId()))
        {
            mapOfBudgets.get(budget.getId()).setFromBudget(budget);
            EventDispatcher.getInstance().notifyExpenceUpdatedListeners();
        }
        else
        {
            this.mapOfBudgets.put(budget.getId(), budget);
        }
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
