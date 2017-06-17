package money.mezu.mezu;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by asafb on 5/12/2017.
 */

public class LanguageUtils {

    public static void setLanguage(String languageCode, Context context){
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        Locale newLocale = new Locale(languageCode.toLowerCase());
        conf.setLocale(newLocale);
        Locale.setDefault(newLocale);
        res.updateConfiguration(conf, dm);

    }

    public static String getDefaultLanguage(Context context){
        return getLanguageFromValue(getISO3CurrentLanguageCode(), context);
    }

    public static String getISO3CurrentLanguageCode(){
        return Locale.getDefault().getISO3Language();
    }

    public static boolean languageValueIsValid(String val){
        return (val.equals("heb") || val.equals("eng"));
    }

    public static String getLanguageFromValue(String lang, Context context){
        if (lang.equals("heb")){
            return context.getString(R.string.hebrew);
        }
        else {
            return context.getString(R.string.english);
        }
    }

    public static String getLanguageCodeFromValue(String lang){
        if (lang.equals("heb")){
            return "he";
        }
        else {
            return "en";
        }
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
}
