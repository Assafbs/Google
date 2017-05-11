package money.mezu.mezu;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)// TODO: change to the version we decide to support
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            SessionManager sessionManager = new SessionManager(getActivity());

            EditTextPreference displayNamePref = (EditTextPreference)findPreference("display_name");
            displayNamePref.setText(sessionManager.getUserName());
            displayNamePref.setSummary(sessionManager.getUserName());
            displayNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    // TODO: update name in DB
                    return false;
                }
            });

            ListPreference languagePref = (ListPreference)findPreference("language");
            languagePref.setSummary(getCurrentLanguage()); // temp
            // TODO: take current value from shared preferences
            languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    // TODO: change language and save in shared preference
                    preference.setSummary(getLanguageFromValue(o.toString()));
                    return true;
                }
            });

            SwitchPreference enableNotificationsPref = (SwitchPreference)findPreference("enable_notifications");
            // TODO: take current value from shared preferences
            enableNotificationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    // TODO: save decision in shared preference
                    return true;
                }
            });
        }

        private String getCurrentLanguage(){
            return getLanguageFromValue(Locale.getDefault().getISO3Language());
        }

        private String getLanguageFromValue(String lang){
            if (lang.equals("heb")){
                return getString(R.string.hebrew);
            }
            else {
                return getString(R.string.english);
            }
        }
    }


}