package money.mezu.mezu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BudgetViewActivity extends BaseNavDrawerActivity {

    protected Budget mCurrentBudget;
    private boolean updateCurrentBudget = false;

    private EditText dateField;
    private EditText timeField;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Calendar c;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    protected TabsViewPagerAdapter mViewPagerAdapter;

    private GraphsTabFragment mGraphsTabFragment;

    public boolean graphShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupCurrentBudget();
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
        ExpensesTabFragment expensesTabFragment = new ExpensesTabFragment();
        mGraphsTabFragment = new GraphsTabFragment();

        mViewPagerAdapter.setupTabsFragments(isRTL(), expensesTabFragment, mGraphsTabFragment);
    }

    //************************************************************************************************************************************************
    private void setupCurrentBudget() {
        Intent intent = getIntent();
        Gson gson = new Gson();
        String json = intent.getStringExtra("budget");
        mCurrentBudget = gson.fromJson(json, Budget.class);
        updateCurrentBudget = true;
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget budget) {
        super.budgetUpdatedCallback(budget);
        if (updateCurrentBudget && mCurrentBudget.getId().equals(budget.getId())) {
            mCurrentBudget = budget;
            updateCurrentBudget = false;
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
            editBudgetIntent.putExtra("curBudgetId",mCurrentBudget.getId());
            editBudgetIntent.putExtra("curBudgetName",mCurrentBudget.getName());
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
                tryReleaseGraph();
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                tryReleaseGraph();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                tryReleaseGraph();
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
    private void showPopupAddExpenseActivity(final Activity context) {
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
                InputMethodManager lManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(EditTextAmount, 0);
            }
        });
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                context.findViewById(R.id.fab_expense).setVisibility(View.VISIBLE);
            }
        });

        dateField = (EditText) layout.findViewById(R.id.EditTextDate);
        timeField = (EditText) layout.findViewById(R.id.EditTextTime);

        // Get Current Time
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Set Current Time to EditTexts
        dateField.setText(DateFormat.getDateInstance().format(c.getTime()));
        timeField.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));

        dateField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
                return pickDate(event);
            }
        });
        timeField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
                return pickTime(event);
            }
        });

        Button add_btn = (Button) layout.findViewById(R.id.add_action_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                addExpense(layout, popUp);
            }
        });

        popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }
    //************************************************************************************************************************************************
    public void addExpense(View layout, PopupWindow popUp) {
        EditText amountField = (EditText) layout.findViewById(R.id.EditTextAmount);
        EditText description = (EditText) layout.findViewById(R.id.EditTextDescription);
        Spinner categorySpinner = (Spinner) layout.findViewById(R.id.SpinnerCategoriesType);
        Log.d("", String.format("tmp:tmp: id: %d", categorySpinner.getId()));
        Category category = Category.getCategoryFromString(categorySpinner.getSelectedItem().toString());
        EditText title = (EditText) layout.findViewById(R.id.EditTextTitle);
        String t_title = title.getText().toString();
        boolean isExpense = true;
        if (t_title.equals("")) {
            t_title = getResources().getString(R.string.general);
        }

        //HANDLE IS EXPENSE/INCOME
        RadioGroup rgExpense = (RadioGroup) layout.findViewById(R.id.radio_expense_group);
        int selectedId = rgExpense.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_income) {
            isExpense = false;
        }

        Double amount;
        if (amountField.getText().toString().equals("")) {
            amount = 0.0;
        } else {
            amount = Double.parseDouble(amountField.getText().toString());
        }
        //CREATE EXPENSE
        Expense newExpense = new Expense("",
                amount,
                t_title,
                description.getText().toString(),
                category,
                c.getTime(),
                mSessionManager.getUserId(),
                mSessionManager.getUserName(),
                isExpense);

        FirebaseBackend.getInstance().addExpenseToBudget(mCurrentBudget, newExpense);
        popUp.dismiss();
    }
    //************************************************************************************************************************************************
    public boolean pickDate(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Launch Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(BudgetViewActivity.this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            c.set(mYear, mMonth, mDay, mHour, mMinute);
                            dateField.setText(DateFormat.getDateInstance().format(c.getTime()));

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
            return true;
        }
        return false;
    }
    //************************************************************************************************************************************************
    public boolean pickTime(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(BudgetViewActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            c.set(mYear, mMonth, mDay, mHour, mMinute);
                            timeField.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
            return true;
        }
        return false;
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

    @Override
    public void onBackPressed(){
        if (!tryReleaseGraph()) {
            super.onBackPressed();
        }
    }

    private boolean tryReleaseGraph() {
        if(graphShown) {
            mViewPagerAdapter.onSwitchFromGraph(mGraphsTabFragment);
            graphShown = false;
            return true;
        }
        return false;
    }
}