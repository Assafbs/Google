package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.math.BigInteger;
import android.net.Uri;

public class SessionManager {
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;

    private GoogleApiClient mGoogleApiClient;

    private static final String PREF_NAME = "MezuPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_ID = "id";
    private static final String KEY_LOGIN_TYPE = "loginType";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LAST_BUDGET = "lastBudget";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void createLoginSession(String name, UserIdentifier id, String logInType, String email, Uri image){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ID, id.getId().toString());
        editor.putString(KEY_LOGIN_TYPE, logInType);
        editor.putString(KEY_EMAIL, email);
        if (image != null)
            editor.putString(KEY_IMAGE, image.toString());
        editor.commit();
    }

    public boolean checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            _context.startActivity(i);
            return false;
        }
        return true;
    }

    public UserIdentifier getUserId(){
        String id = pref.getString(KEY_ID, null);
        if (id == null)
            return null;
        return new UserIdentifier(new BigInteger(id));
    }

    public String getLoginType() {
        return pref.getString(KEY_LOGIN_TYPE, null);
    }

    public String getUserName(){
        return pref.getString(KEY_NAME, null);
    }

    public String getUserEmail(){ return pref.getString(KEY_EMAIL, null); }

    public Uri getUserImage()
    {
        Uri image = null;
        String imageString = pref.getString(KEY_IMAGE, null);
        if (imageString != null) {
            image = Uri.parse(imageString);
        }
        return image;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        FirebaseBackend.getInstance().resetBackend();
        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
        ((Activity)_context).overridePendingTransition(0, 0);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLastBudget (Budget budget) {
        Gson gson = new Gson();
        String json = gson.toJson(budget);
        setLastBudget(json);
    }

    public void setLastBudget (String json) {
        editor.putString(KEY_LAST_BUDGET, json);
        editor.commit();
    }

    public boolean goToLastBudget() {
        String json = pref.getString(KEY_LAST_BUDGET, null);
        if (json == null)
            return false;
        Intent i = new Intent(_context, BudgetViewActivity.class);
        i.putExtra("budget", json);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
        ((Activity)_context).overridePendingTransition(0, 0);
        return true;
    }
}