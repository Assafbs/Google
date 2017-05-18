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
import java.util.HashMap;

public class BudgetsActivity extends BaseNavDrawerActivity implements  BudgetUpdatedListener{

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

    }

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

    //************************************************************************************************************************************************

}
