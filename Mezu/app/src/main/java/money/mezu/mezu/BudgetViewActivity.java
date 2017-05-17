package money.mezu.mezu;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BudgetViewActivity extends BaseNavDrawerActivity {

    protected static Budget mCurrentBudget;

    private EditText dateField;
    private EditText timeField;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Calendar c;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabsViewPagerAdapter mViewPagerAdapter;

    private GraphsTabFragment mGraphsTabFragment;

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
        ExpensesTabFragment expensesTabFragment = new ExpensesTabFragment();
        mGraphsTabFragment = new GraphsTabFragment();
        expensesTabFragment.setCurrentBudget(mCurrentBudget);
        mGraphsTabFragment.setCurrentBudget(mCurrentBudget);

        mViewPagerAdapter.setupTabsFragments(isRTL(), expensesTabFragment, mGraphsTabFragment);
    }

    //************************************************************************************************************************************************
    public static void setCurrentBudget(Budget budget) {
        mCurrentBudget = budget;
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

        popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);

        dateField = (EditText) layout.findViewById(R.id.EditTextDate);
        timeField = (EditText) layout.findViewById(R.id.EditTextTime);
        // Get Current Time
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        dateField.setOnTouchListener(new View.OnTouchListener() {

            //NOTHING IS HAPPENING!!!!!
            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
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

        });
        timeField.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
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
        });


        Button add_btn = (Button) layout.findViewById(R.id.add_action_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
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

                mGraphsTabFragment.calculatePieDataSet();
                mGraphsTabFragment.mPieChart.notifyDataSetChanged();
                mGraphsTabFragment.mPieChart.invalidate();

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

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        if (isRTL()) {
            mTabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            mViewPager.setCurrentItem(2);
        }
    }

    private static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    private static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

}