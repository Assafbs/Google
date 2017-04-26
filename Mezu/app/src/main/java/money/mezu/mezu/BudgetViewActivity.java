package money.mezu.mezu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

/**
 * Created by davidled on 21/04/2017.
 */

public class BudgetViewActivity extends AppCompatActivity {
    //    protected static BudgetIdentifier currentID;
    protected static Budget currentBudget;
    boolean isClicked = true;
    PopupWindow popUpWindow;
    ViewGroup.LayoutParams layoutParams;
    LinearLayout mainLayout;
    Button btnClickHere;
    LinearLayout containerLayout;
    TextView tvMsg;

    private SessionManager sessionManager;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        containerLayout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        popUpWindow = new PopupWindow(this);

        TextView budgetName = (TextView) findViewById(R.id.budgetViewName);
        budgetName.setText(currentBudget.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked) {
                    isClicked = false;
                    popUpWindow.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
                    popUpWindow.update(50, 50, 320, 90);
                } else {
                    isClicked = true;
                    popUpWindow.dismiss();
                }
            }
        });

        popUpWindow.setContentView(containerLayout);

        // Create the adapter to convert the array to views
        ExpenseAdapter adapter = new ExpenseAdapter(this, currentBudget.getExpenses());
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.expenses_list);
        listView.setAdapter(adapter);

        adapter.clear();
        Expense e1 = new Expense(new ExpenseIdentifier(11111), 100, Category.FOOD, "ASSAFIM");
        adapter.add(e1);
        Expense e2 = new Expense(new ExpenseIdentifier(11112), 1000, Category.DEBT_REDUCTION, "LIORIM");
        adapter.add(e2);
        Expense e3 = new Expense(new ExpenseIdentifier(11113), 600, Category.CLOTHING, "ME");
        adapter.add(e3);
        Expense e4 = new Expense(new ExpenseIdentifier(11114), 750, Category.HOUSEHOLD_SUPPLIES, "SNIRIM");
        adapter.add(e4);
        Expense e5 = new Expense(new ExpenseIdentifier(11115), 69.69, Category.PERSONAL, "ME");
        adapter.add(e5);
        Expense e6 = new Expense(new ExpenseIdentifier(11116), 6969.6969, Category.ENTERTAINMENT, "DAVIDIM");
        adapter.add(e6);

        sessionManager = new SessionManager(this);
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

    }

    public static void setCurrentBudget(Budget budget) {
        currentBudget = budget;
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
}
