package money.mezu.mezu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

/**
 * Created by davidled on 21/04/2017.
 */

public class BudgetViewActivity extends AppCompatActivity {
    protected static  BudgetIdentifier currentID;
    protected static Budget currentBudget;
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
                // TODO: add new expense
                Toast.makeText(BudgetViewActivity.this, "TODO: add new expense", Toast.LENGTH_SHORT).show();
            }
        });

        //// Create the adapter to convert the array to views
        ExpenseAdapter adapter = new ExpenseAdapter(this, currentBudget.getExpenses());
        //// Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.expenses_list);
        listView.setAdapter(adapter);


        Expense e1 = new Expense(new ExpenseIdentifier(11111), 100, Category.FOOD);
        adapter.add(e1);
        Expense e2 = new Expense(new ExpenseIdentifier(11112), 1000, Category.DEBT_REDUCTION);
        adapter.add(e2);
        Expense e3 = new Expense(new ExpenseIdentifier(11113), 600, Category.CLOTHING);
        adapter.add(e3);
        Expense e4 = new Expense(new ExpenseIdentifier(11114), 750, Category.HOUSEHOLD_SUPPLIES);
        adapter.add(e4);
        Expense e5 = new Expense(new ExpenseIdentifier(11115), 69.69, Category.PERSONAL);
        adapter.add(e5);
        Expense e6 = new Expense(new ExpenseIdentifier(11116), 6969.6969, Category.ENTERTAINMENT);
        adapter.add(e6);

    }

    public static void setCurrentBudget(Budget budget){
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
//            logout();
        }
        return super.onOptionsItemSelected(item);
    }





}
