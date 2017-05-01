package money.mezu.mezu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import java.math.BigInteger;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;

    GoogleApiClient mGoogleApiClient;

    private static final String PREF_NAME = "MezuPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_LOGIN_TYPE = "loginType";

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

    public void createLoginSession(String name, UserIdentifier id, String logInType){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_ID, id.getId().toString());
        editor.putString(KEY_LOGIN_TYPE, logInType);
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

    public void logoutUser(){
        editor.clear();
        editor.commit();
        FirebaseBackend.getInstance().stopListeningOnEvents();
        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}