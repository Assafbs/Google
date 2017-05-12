package money.mezu.mezu;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;

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

            Context context = getActivity();
            ListPreference languagePref = (ListPreference)findPreference("language");
            String curLanguage = languagePref.getValue();
            if (!LanguageUtils.languageValueIsValid(curLanguage)){
                curLanguage = LanguageUtils.getDefaultLanguage(context);
            }
            languagePref.setSummary(LanguageUtils.getLanguageFromValue(curLanguage, context));
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

                    return true;
                }
            });

            SwitchPreference enableNotificationsPref = (SwitchPreference)findPreference("enable_notifications");
            enableNotificationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return true;
                }
            });
        }
    }


}