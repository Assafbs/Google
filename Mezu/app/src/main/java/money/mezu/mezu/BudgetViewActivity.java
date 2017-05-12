package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Calendar;

public class BudgetViewActivity extends BaseNavDrawerActivity {

    protected static Budget mCurrentBudget;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabsViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(mCurrentBudget.getName());

        setContentView(R.layout.activity_budget_view);

        setupTabs();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
                showPopupAddExpenseActivity(BudgetViewActivity.this);
            }
        });

        // Create the tabs that will be shown
        // Currently only one tab is needed, more should be added here
        ExpensesTabFragment expenseTabFragment = new ExpensesTabFragment();
        expenseTabFragment.setCurrentBudget(mCurrentBudget);

        mViewPagerAdapter.setupTabsFragments(expenseTabFragment);
    }
    //************************************************************************************************************************************************
    public static void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
    }
    //************************************************************************************************************************************************
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

                FirebaseBackend.getInstance().addExpenseToBudget(mCurrentBudget, newExpense);
                popUp.dismiss();
            }
        });
    }
    //************************************************************************************************************************************************
    private void setupTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mViewPagerAdapter = new TabsViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        final TabLayout.Tab expensesTab = mTabLayout.newTab();
        final TabLayout.Tab graphsTab = mTabLayout.newTab();
        final TabLayout.Tab reviewTab = mTabLayout.newTab();

        expensesTab.setText(R.string.expenses_tab_title);
        graphsTab.setText(R.string.graphs_tab_title);
        reviewTab.setText(R.string.review_tab_title);

        mTabLayout.addTab(expensesTab, 0);
        mTabLayout.addTab(graphsTab, 1);
        mTabLayout.addTab(reviewTab, 2);

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

}