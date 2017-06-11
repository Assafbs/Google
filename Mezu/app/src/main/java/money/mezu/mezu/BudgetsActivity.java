package money.mezu.mezu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class BudgetsActivity extends BaseNavDrawerActivity implements  BudgetUpdatedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d("","BudgetsActivity::onCreate start");
        setLanguage();
        super.onCreate(savedInstanceState);
        StaticContext.mContext = getApplicationContext();
        // This code will make the app go to the login screen if the user is not connected
        mSessionManager = new SessionManager(this);
        if(!mSessionManager.checkLogin()) {
            return;
        }
        BackendCache.getInstatnce();
        // Try to go to the last budget saved to session manager
        if (mSessionManager.goToLastBudget()){
            return;
        }

        setContentView(R.layout.activity_budgets);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBudgetIntent = new Intent(BudgetsActivity.this, AddBudgetActivity.class);
                startActivity(addBudgetIntent);
            }
        });
        this.mapOfBudgets = BackendCache.getInstatnce().getBudgets();
        if (this.mapOfBudgets.size() > 0){
            clearNoBudgetsIndication();
        }
        ListView listView = (ListView) findViewById(R.id.budgets_list_large);
        BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
        listView.setAdapter(adapter);
    }
    //************************************************************************************************************************************************
    private void setLanguage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String language = sharedPref.getString("language", "");
        if (!language.isEmpty()){
            if (!LanguageUtils.getISO3CurrentLanguageCode().equals(language)){
                String languageCode = LanguageUtils.getLanguageCodeFromValue(language);
                LanguageUtils.setLanguage(languageCode, getApplicationContext());
            }
        }
    }

    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget budget)
    {
        Log.d("",String.format("BudgetsActivity:budgetUpdatedCallback: invoked with budget: %s", budget.toString()));
        super.budgetUpdatedCallback(budget);
        clearNoBudgetsIndication();
        ListView listView = (ListView) findViewById(R.id.budgets_list_large);
        BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
        listView.setAdapter(adapter);
    }
    //************************************************************************************************************************************************
    private void clearNoBudgetsIndication(){
        findViewById(R.id.explaining_text).setVisibility(View.GONE);
        findViewById(R.id.logo).setVisibility(View.GONE);
    }
}
