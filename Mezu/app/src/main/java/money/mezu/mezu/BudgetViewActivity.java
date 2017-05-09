package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Calendar;

public class BudgetViewActivity extends BaseNavDrawerActivity {
    protected static Budget currentBudget;

    private ExpenseAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_view);

        TextView budgetName = (TextView) findViewById(R.id.budgetViewName);
        budgetName.setText(currentBudget.toString());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                showPopupAddExpenseActivity(BudgetViewActivity.this);
            }
        });

        // Create the adapter to convert the array to views
        mAdapter = new ExpenseAdapter(this, currentBudget.getExpenses());
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.expenses_list);
        listView.setAdapter(mAdapter);
    }

    public static void setCurrentBudget(Budget budget) {
        currentBudget = budget;
    }

    private void showPopupAddExpenseActivity(final Activity context)
    {
        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_add_expense, null);

        // Creating the PopupWindow
        final PopupWindow popUp = new PopupWindow(context);
        popUp.setContentView(layout);
        popUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setFocusable(true);
        final EditText EditTextAmount = (EditText) layout.findViewById(R.id.EditTextAmount);
        EditTextAmount.post(new Runnable() {
            public void run() {
                EditTextAmount.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(EditTextAmount, 0);
            }
        });
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                context.findViewById(R.id.fab_expense).setVisibility(View.VISIBLE);
            }
        });

        popUp.showAtLocation(layout, Gravity.CENTER,0,0);
        Button add_btn=(Button)layout.findViewById(R.id.add_action_btn);
        add_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                EditText amountField = (EditText)layout.findViewById(R.id.EditTextAmount);
                EditText description = (EditText)layout.findViewById(R.id.EditTextDescription);
                Spinner categorySpinner =(Spinner) layout.findViewById(R.id.SpinnerCategoriesType);
                Log.d("", String.format("tmp:tmp: id: %d", categorySpinner.getId()));
                Category category = Category.getCategoryFromString(categorySpinner.getSelectedItem().toString());
                EditText title = (EditText)layout.findViewById(R.id.EditTextTitle);
                String t_title = title.getText().toString();
                boolean isExpense = true;
                if (t_title.equals("")){
                    t_title = getResources().getString(R.string.general);
                }

                RadioGroup rgExpense = (RadioGroup) layout.findViewById(R.id.radio_expense_group);
                int selectedId = rgExpense.getCheckedRadioButtonId();
                if (selectedId == R.id.radio_income){
                    isExpense = false;
                }

                Expense newExpense = new Expense("",
                        Double.parseDouble(amountField.getText().toString()),
                        t_title,
                        description.getText().toString(),
                        category,
                        Calendar.getInstance().getTime(),
                        mSessionManager.getUserId(),
                        mSessionManager.getUserName(),
                        isExpense);

                FirebaseBackend.getInstance().addExpenseToBudget(currentBudget, newExpense);
                currentBudget.addExpense(newExpense);
                mAdapter.notifyDataSetChanged();
                popUp.dismiss();
            }
        });
    }
}
