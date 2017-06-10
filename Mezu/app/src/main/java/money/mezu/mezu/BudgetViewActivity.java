package money.mezu.mezu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.util.Locale;

public class BudgetViewActivity extends BaseNavDrawerActivity implements ExpenseUpdatedListener {

    protected Budget mCurrentBudget;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    protected TabsViewPagerAdapter mViewPagerAdapter;

    private GraphsTabFragment mGraphsTabFragment;
    private ExpensesTabFragment mExpensesTabFragment;
    public boolean graphShown = false;
    public boolean expenseShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupCurrentBudget();
        this.setTitle(mCurrentBudget.getName());

        setContentView(R.layout.activity_budget_view);
        showBalanceInToolbar();
        EventDispatcher.getInstance().registerExpenseUpdateListener(this);

        setupTabs();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddExpenseFragment();
            }
        });

        // Create the tabs that will be shown
        mExpensesTabFragment = new ExpensesTabFragment();
        mGraphsTabFragment = new GraphsTabFragment();

        mViewPagerAdapter.setupTabsFragments(isRTL(), mExpensesTabFragment, mGraphsTabFragment);
    }
    //************************************************************************************************************************************************
    public void expenseUpdatedCallback() {
        Log.d("","BudgetViewActivity:expenseUpdatedCallback: invoked");
        showBalanceInToolbar();
    }
    //************************************************************************************************************************************************
    private void setupCurrentBudget() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        String json = intent.getStringExtra("budget");
        mCurrentBudget = gson.fromJson(json, Budget.class);
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget budget) {
        Log.d("",String.format("BudgetViewActivity:budgetUpdatedCallback: invoked with budget: %s", budget.toString()));

        super.budgetUpdatedCallback(budget);
        if (mCurrentBudget.getId().equals(budget.getId())) {
            mCurrentBudget = budget;
            showBalanceInToolbar();
            mExpensesTabFragment.filterExpenses();
        }
    }
    //************************************************************************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.budget_view_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_edit)
        {
            Intent editBudgetIntent = new Intent(this, EditBudgetActivity.class);
            Gson gson = new Gson();
            String json =  gson.toJson(mCurrentBudget);
            editBudgetIntent.putExtra("budget", json);
            startActivity(editBudgetIntent);
        }
        return true;
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

        if (isRTL()) {
            mTabLayout.addTab(reviewTab, 0);
            mTabLayout.addTab(graphsTab, 1);
            mTabLayout.addTab(expensesTab, 2);
        } else {
            mTabLayout.addTab(expensesTab, 0);
            mTabLayout.addTab(graphsTab, 1);
            mTabLayout.addTab(reviewTab, 2);
        }

        mTabLayout.setTabTextColors(ContextCompat.getColorStateList(this, R.color.tab_selector));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.indicator));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tryReleaseTabs();
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                tryReleaseTabs();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                tryReleaseTabs();
            }
        });

        if (isRTL()) {
            mTabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            mViewPager.setCurrentItem(2);
        }
    }
    //************************************************************************************************************************************************
    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }
    //************************************************************************************************************************************************
    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }
    //************************************************************************************************************************************************
    private void showAddExpenseFragment() {
        ExpenseFragment expenseFragment = new ExpenseFragment();
        expenseFragment.isAdd = true;
        setExpenseFragment(expenseFragment);
    }

    public void setExpenseFragment(ExpenseFragment expenseFragment) {
        TabLayout.Tab ExpensesTab = mTabLayout.getTabAt(isRTL()? 2 : 0);
        ExpensesTab.select();
        expenseShown = true;
        mViewPagerAdapter.onSwitchToExpense(expenseFragment);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
        fab.setVisibility(View.INVISIBLE);
    }
    //************************************************************************************************************************************************
    @Override
    public void onBackPressed(){
        if (!tryReleaseTabs()) {
            super.onBackPressed();
        }
    }
    //************************************************************************************************************************************************
    public boolean tryReleaseTabs() {
        if(graphShown) {
            mViewPagerAdapter.onSwitchFromGraph(mGraphsTabFragment);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
            fab.setVisibility(View.VISIBLE);
            graphShown = false;
            return true;
        } else if (expenseShown) {
            mViewPagerAdapter.onSwitchFromExpense(mExpensesTabFragment);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_expense);
            fab.setVisibility(View.VISIBLE);
            expenseShown = false;
            return true;
        }
        return false;
    }
    //************************************************************************************************************************************************
    private void showBalanceInToolbar() {
        double balance = mCurrentBudget.getCurrentBalance();
        String balanceString = String.valueOf(balance);
        if (balance > 0) {
            balanceString = "+" + balanceString;
        }
        mToolbar.setSubtitle(balanceString);
    }
    //************************************************************************************************************************************************
    public static void goToBudgetView (Context context, Budget budget, SessionManager sessionManager) {
        Intent budgetViewIntent = new Intent(context, BudgetViewActivity.class);
        budgetViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Gson gson = new Gson();
        String json =  gson.toJson(budget);
        budgetViewIntent.putExtra("budget", json);
        context.startActivity(budgetViewIntent);
        sessionManager.setLastBudget(json);
    }
}