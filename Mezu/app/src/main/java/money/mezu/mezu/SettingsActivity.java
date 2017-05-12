package money.mezu.mezu;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

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
            String curLanguage = languagePref.getValue();
            if (!languageValueIsValid(curLanguage)){
                curLanguage = getDefaultLanguage();
            }
            languagePref.setSummary(getLanguageFromValue(curLanguage));
            // TODO: take current value from shared preferences
            languagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (((ListPreference)preference).getValue().equals(o.toString())){
                        return true; // current language chosen again - nothing to change
                    }
                    // TODO: save in shared preference and change accordingly when app starts
                    String languageCode = getLanguageCodeFromValue(o.toString());
                    setLanguage(languageCode);

                    // restart app to apply change in language:
                    Intent restartIntent = getActivity().getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getActivity().getPackageName() );
                    restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(restartIntent);

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

        private String getDefaultLanguage(){
            return getLanguageFromValue(getISO3CurrentLanguageCode());
        }

        private String getISO3CurrentLanguageCode(){
            return Locale.getDefault().getISO3Language();
        }

        private boolean languageValueIsValid(String val){
            return (val.equals("heb") || val.equals("eng"));
        }

        private String getLanguageFromValue(String lang){
            if (lang.equals("heb")){
                return getString(R.string.hebrew);
            }
            else {
                return getString(R.string.english);
            }
        }

        private String getLanguageCodeFromValue(String lang){
            if (lang.equals("heb")){
                return "he";
            }
            else {
                return "en";
            }
        }

        private void setLanguage(String languageCode){
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(languageCode.toLowerCase()));
            res.updateConfiguration(conf, dm);
        }
    }


}