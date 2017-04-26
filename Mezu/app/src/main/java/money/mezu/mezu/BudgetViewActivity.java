package money.mezu.mezu;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    protected static Budget currentBudget;
    boolean isClicked = true;


    private SessionManager sessionManager;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView budgetName = (TextView) findViewById(R.id.budgetViewName);
        budgetName.setText(currentBudget.toString());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked) {
                    isClicked = false;
                    showSortPopup(BudgetViewActivity.this, new Point(100, 100));
                } else {
                    isClicked = true;
                }
            }
        });


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

    private void showSortPopup(final Activity context, Point p) {
        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.activity_add_expense);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.activity_add_expense, viewGroup);

        // Creating the PopupWindow
        PopupWindow popUp = new PopupWindow(context);
        popUp.setContentView(layout);
        popUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);


        popUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 95;

        // Clear the default translucent background
//        popUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

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
