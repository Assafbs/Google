package money.mezu.mezu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.math.BigInteger;
import java.util.ArrayList;

public class BudgetsActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;

    private SessionManager sessionManager;
    private BackendInterface backend = FirebaseBackend.getInstance();

    private ArrayList<Budget> arrayOfBudgets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This code will make the app go to the login screen if the user is not connected
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setContentView(R.layout.activity_budgets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addBudgetIntent = new Intent(BudgetsActivity.this, AddBudgetActivity.class);
                startActivity(addBudgetIntent);
            }
        });


        // Construct the data source
        arrayOfBudgets = backend.getUsersBudgets(sessionManager.getUserId());
        // Create the adapter to convert the array to views
        BudgetAdapter adapter = new BudgetAdapter(this, arrayOfBudgets);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.budgets_list);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Open Settings ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_log_out) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        if (sessionManager.getLoginType().equals("Google")) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                        }
                    });
        }
        sessionManager.logoutUser();
    }

    public Budget getBudgetByID(BudgetIdentifier id) {
        for (Budget budget : arrayOfBudgets) {
            if (budget.getId().equals(id)) {
                return budget;
            }
        }
        //ERROR MESSAGE
        return null;
    }
}
