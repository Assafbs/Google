package money.mezu.mezu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    private void setStatusBarColor(){
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            SessionManager sessionManager = new SessionManager(getActivity());

            EditTextPreference displayNamePref = (EditTextPreference)findPreference("display_name");
            displayNamePref.setText(sessionManager.getUserName());
            displayNamePref.setSummary(sessionManager.getUserName());

            Context context = getActivity();
            ListPreference languagePref = (ListPreference)findPreference("language");
            String curLanguage = languagePref.getValue();
            if (!LanguageUtils.languageValueIsValid(curLanguage)){
                curLanguage = LanguageUtils.getDefaultLanguage(context);
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String language = sharedPref.getString("language", "");
            languagePref.setSummary(LanguageUtils.getLanguageFromValue(curLanguage, context));
            languagePref.setValueIndex(language.equals("heb")?0:1);
            languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (((ListPreference)preference).getValue().equals(o.toString())){
                        return true; // current language chosen again - nothing to change
                    }
                    String languageCode = LanguageUtils.getLanguageCodeFromValue(o.toString());
                    LanguageUtils.setLanguage(languageCode, getActivity());

                    // restart app to apply change in language:
                    Intent restartIntent = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getPackageName() );
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(restartIntent);

                    Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(settingsIntent);
                    ExpensesTabFragment.sDefaultDate = true;
                    return false;
                }
            });

            SwitchPreference enableNotificationsPref = (SwitchPreference)findPreference("enable_notifications_on_expenses");
            enableNotificationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    SessionManager sessionManager = new SessionManager(StaticContext.mContext);
                    boolean isChecked = o.equals(true);
                    FirebaseBackend.getInstance().setShouldNotifyOnTransaction(isChecked, sessionManager.getUserId());
                    if (isChecked){
                        findPreference("minimum_amount").setEnabled(true);
                    }
                    else{
                        findPreference("minimum_amount").setEnabled(false);
                    }
                    return false;
                }
            });

            EditTextPreference minimumAmountPref = (EditTextPreference)findPreference("minimum_amount");
            if (!enableNotificationsPref.isChecked()){
                minimumAmountPref.setEnabled(false);
            }
            minimumAmountPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    SessionManager sessionManager = new SessionManager(StaticContext.mContext);
                    FirebaseBackend.getInstance().setMinimalTransactionNotificationValue(Integer.parseInt((String)o), sessionManager.getUserId());
                    return false;
                }
            });

            SwitchPreference enableNotificationsOnBudgets = (SwitchPreference)findPreference("enable_notifications_on_budgets");
            enableNotificationsOnBudgets.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    SessionManager sessionManager = new SessionManager(StaticContext.mContext);
                    boolean isChecked = o.equals(true);
                    FirebaseBackend.getInstance().setShouldNotifyWhenAddedToBudget(isChecked, sessionManager.getUserId());
                    return false;
                }
            });
            
        }
    }


}