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
import android.widget.Toast;

import java.util.ArrayList;

public class BudgetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgets);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add new budget
                Toast.makeText(BudgetsActivity.this, "TODO: add new budget",Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> budgets = new ArrayList<>();
        // TODO: Replace with real budgets
        budgets.add("Budget 1");
        budgets.add("Budget 2");
        budgets.add("Budget 3");
        budgets.add("Budget 4");
        budgets.add("Budget 5");
        budgets.add("Budget 6");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.addAll(budgets);
        ListView budgetsList = (ListView)findViewById(R.id.budgets_list);
        budgetsList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Toast.makeText(this,"Open Settings ", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.action_log_out){
            // TODO: log user out
            finish(); // will return to sign in activity;
                      // maybe will need to be changed later
        }
        return super.onOptionsItemSelected(item);
    }
}
