package money.mezu.mezu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class ExpenseFragment extends Fragment {
    private CategoryPredictor predictor;
    private boolean choseCategory = false;
    private View mView;

    EditText mEditTextAmount;
    EditText mEditTextTitle;
    Spinner mCategorySpinner;
    EditText mEditTextDate;
    EditText mEditTextTime;
    EditText mEditTextDescription;
    RadioGroup mRGIsExpense;
    RadioButton mRBExpense;
    RadioButton mRBIncome;
    Button mAddButton;
    Button mEditButton;
    EditText mEditTextAddedBy;
    ImageView mRepeatAction;
    TextView mRepeatText;
    int mRepeatChoice;
    android.support.design.widget.TextInputLayout mAddedByLayout;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Calendar c;
    private boolean incomeSelected = false;
    private Category incomeCat = Category.CATEGORY;
    private Category expenseCat = Category.CATEGORY;
    private ArrayAdapter<String> incomeAdapter;
    private ArrayAdapter<String> expenseAdapter;
    private DateFormat mDateFormat;
    private Expense expenseToShow = null;
    public boolean isAdd;
    private BudgetViewActivity mActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (BudgetViewActivity) getActivity();
        mView = inflater.inflate(R.layout.activity_add_expense, null);

        mEditTextAmount = (EditText) mView.findViewById(R.id.EditTextAmount);
        mEditTextTitle = (EditText) mView.findViewById(R.id.EditTextTitle);
        mCategorySpinner = (Spinner) mView.findViewById(R.id.SpinnerCategoriesType);
        mEditTextDate = (EditText) mView.findViewById(R.id.EditTextDate);
        mEditTextTime = (EditText) mView.findViewById(R.id.EditTextTime);
        mEditTextDescription = (EditText) mView.findViewById(R.id.EditTextDescription);
        mRGIsExpense = (RadioGroup) mView.findViewById(R.id.radio_expense_group);
        mAddButton = (Button) mView.findViewById(R.id.add_action_btn);
        mRBExpense = (RadioButton) mView.findViewById(R.id.radio_expense);
        mRBIncome = (RadioButton) mView.findViewById(R.id.radio_income);
        mEditTextAddedBy = (EditText) mView.findViewById(R.id.added_by_edit_text);
        mEditButton = (Button) mView.findViewById(R.id.edit_action_btn);
        mRepeatAction = (ImageView) mView.findViewById(R.id.repeat_action);
        mRepeatText = (TextView) mView.findViewById(R.id.ratio_text);
        mAddedByLayout = (android.support.design.widget.TextInputLayout) mView.findViewById(R.id.added_by_layout);
        mRepeatChoice = 0;
        mDateFormat = LanguageUtils.isRTL() ? new SimpleDateFormat("HH:mm", Locale.getDefault()) : new SimpleDateFormat("h:mm a", Locale.getDefault());
        if (isAdd) {
            setupAddExpense();
        } else {
            setupShowExpense();
        }
        return mView;
    }

    public void setShowExpense(Expense expenseToShow) {
        this.expenseToShow = expenseToShow;
        isAdd = false;
    }

    private void setupShowExpense() {
        String titleString = expenseToShow.getTitle();
        if (titleString == null) {
            titleString = "General";
        }
        mEditTextTitle.setText(titleString);

        mAddButton.setText(getResources().getString(R.string.delete));
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "ExpenseFragment: clicked delete expense");
                deleteExpense();
            }
        });
        mEditButton.setVisibility(View.VISIBLE);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "ExpenseFragment: clicked edit expense");
                setupEditExpense();
            }
        });

        mRepeatAction.setVisibility(View.INVISIBLE);

        if (expenseToShow.getAmount() == 0.0) {
            mEditTextAmount.setText("0.0");
        } else {
            mEditTextAmount.setText(String.format("%s", expenseToShow.getAmount()));
        }

        ArrayList<String> categories = new ArrayList<>();

        categories.add(expenseToShow.getCategory().getEmojiWithName());
        mCategorySpinner.setAdapter(new ArrayAdapter<>(mActivity, R.layout.category_spinner_item, categories));

        mRBExpense.setClickable(false);
        mRBIncome.setClickable(false);

        if (expenseToShow.getIsExpense()) {
            mRBExpense.setChecked(true);
            mRBIncome.setChecked(false);
        } else {
            mRBExpense.setChecked(false);
            mRBIncome.setChecked(true);
        }

        mEditTextAddedBy.setText(expenseToShow.getUserName());
        mEditTextDescription.setText(expenseToShow.getDescription());


        mEditTextDate.setText(DateFormat.getDateInstance().format(expenseToShow.getTime()));
        mEditTextTime.setText(mDateFormat.format(expenseToShow.getTime()));

        ViewGroup viewGroup = (ViewGroup) mView.findViewById(R.id.activity_add_expense);
        disableAllFields(viewGroup);
    }

    private void disableAllFields(ViewGroup viewGroup) {
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup)
                disableAllFields((ViewGroup) child);
            if (child != null) {
                if (child.getId() != R.id.add_action_btn && child.getId() != R.id.edit_action_btn) {
                    child.setEnabled(false);
                }
            }
        }
    }

    private void enableAllFields(ViewGroup viewGroup) {
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup)
                enableAllFields((ViewGroup) child);
            if (child != null) {
                child.setEnabled(true);
            }
        }
    }

    private void setupAddExpense() {
        predictor = new CategoryPredictor(mActivity, mCategorySpinner, mActivity.mCurrentBudget);
        mEditButton.setVisibility(View.GONE);
        mAddedByLayout.setVisibility(View.GONE);
        mEditTextTitle.addTextChangedListener(predictor.getWatcher());

        mRepeatAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRepeatPopup();
            }
        });

        mEditTextAmount.post(new Runnable() {
            public void run() {
                mEditTextAmount.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(mEditTextAmount, 0);
            }
        });

        // Get Current Time
        c = Calendar.getInstance();
        c.set(Calendar.MONTH, mActivity.mMonth);
        c.set(Calendar.YEAR, mActivity.mYear);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Set Current Time to EditTexts
        mEditTextDate.setText(DateFormat.getDateInstance().format(c.getTime()));
        mEditTextTime.setText(mDateFormat.format(c.getTime()));

        setupTimeAndDateOnTouchListeners();

        setupRadioButtonsAndSpinner(true);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                addExpense();
            }
        });
        mCategorySpinner.setTag(0);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0 && ((int) adapterView.getTag()) != i) {
                    choseCategory = true;
                    predictor.disable();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupEditExpense() {
        mEditTextTitle.setText(expenseToShow.getTitle());
        mEditButton.setText(getResources().getString(R.string.save));
        ViewGroup viewGroup = (ViewGroup) mView.findViewById(R.id.activity_add_expense);
        enableAllFields(viewGroup);
        mRBExpense.setClickable(true);
        mRBIncome.setClickable(true);

        c = Calendar.getInstance();
        // Get Expense Time
        mYear = expenseToShow.getYear();
        mMonth = expenseToShow.getMonth();
        mDay = expenseToShow.getDay();
        mHour = expenseToShow.getHour();
        mMinute = expenseToShow.getMinute();
        c.set(mYear, mMonth, mDay, mHour, mMinute);

        setupTimeAndDateOnTouchListeners();

        setupRadioButtonsAndSpinner(expenseToShow.getIsExpense());

        mCategorySpinner.setSelection(expenseToShow.getCategory().getSpinnerLocation(!expenseToShow.getIsExpense()));

        if (expenseToShow.getDescription().equals("")) {
            mEditTextDescription.setHint(getResources().getString(R.string.expense_description));
        } else {
            mEditTextDescription.setText(expenseToShow.getDescription());
        }

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                editExpense();
            }
        });

        mEditTextAddedBy.setEnabled(false);
    }

    private void setupTimeAndDateOnTouchListeners() {
        mEditTextDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
                return pickDate(event);
            }
        });
        mEditTextTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg1, MotionEvent event) {
                return pickTime(event);
            }
        });
    }

    private void setupRadioButtonsAndSpinner(boolean isExpense) {
        expenseAdapter = new SpinnerAdapter(mActivity, R.layout.category_spinner_item, Category.getExpenseCategoriesStringAndEmojiList());
        incomeAdapter = new SpinnerAdapter(mActivity, R.layout.category_spinner_item, Category.getIncomeCategoriesStringAndEmojiList());
        mCategorySpinner.setAdapter(isExpense ? expenseAdapter : incomeAdapter);
        mCategorySpinner.setSelection(0);

        mRGIsExpense.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_income & !incomeSelected) {
                    if (isAdd) {
                        predictor.disable();
                    }
                    expenseCat = Category.getCategoryFromString(mCategorySpinner.getSelectedItem().toString());
                    mCategorySpinner.setAdapter(incomeAdapter);
                    mCategorySpinner.setSelection(incomeCat.getSpinnerLocation(true));
                    incomeSelected = true;
                } else {
                    if (!choseCategory && isAdd) {
                        predictor.enable();
                    }
                    incomeCat = Category.getCategoryFromString(mCategorySpinner.getSelectedItem().toString());
                    mCategorySpinner.setAdapter(expenseAdapter);
                    mCategorySpinner.setSelection(expenseCat.getSpinnerLocation(false));
                    incomeSelected = false;
                }
            }
        });
    }

    public boolean pickDate(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Launch Date Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            c.set(mYear, mMonth, mDay, mHour, mMinute);
                            mEditTextDate.setText(DateFormat.getDateInstance().format(c.getTime()));

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
            return true;
        }
        return false;
    }

    public boolean pickTime(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mHour = hourOfDay;
                            mMinute = minute;
                            c.set(mYear, mMonth, mDay, mHour, mMinute);
                            mEditTextTime.setText(mDateFormat.format(c.getTime()));
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
            return true;
        }
        return false;
    }

    public void addExpense() {
        int originalDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        Expense newExpense = createExpenseFromFields();
        int i;
        if (newExpense == null) {
            return;
        }
        HashMap<String,Object> period = new HashMap<>();
        period.put("almostUniqueId", (new Random()).nextDouble());
        period.put("isFirst", true);
        switch (mRepeatChoice) {
            case 1: //every day
                period.put("recurrenceTime", "daily");
                break;
            case 2: //every week
                period.put("recurrenceTime", "weekly");
                break;
            case 3: //every two weeks
                period.put("recurrenceTime", "biweekly");
                break;
            case 4: //every month
                period.put("recurrenceTime", "monthly");
                break;
            case 5: //every two months
                period.put("recurrenceTime", "bimonthly");
                break;
        }
        if (0 != mRepeatChoice)
        {
            newExpense.setRecurrence(period);
        }
        FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
        period.put("isFirst", false);
        switch (mRepeatChoice) {
            case 1: //every day
                for (i = 0; i < 364; i++) {
                    c.add(Calendar.DATE, 1);
                    newExpense.setTime(c.getTime());
                    newExpense.setRecurrence(period);
                    FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
                }
                break;
            case 2: //every week
                for (i = 0; i < 51; i++) {
                    c.add(Calendar.WEEK_OF_YEAR, 1);
                    newExpense.setTime(c.getTime());
                    newExpense.setRecurrence(period);
                    FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
                }
                break;
            case 3: //every two weeks
                for (i = 0; i < 25; i++) {
                    c.add(Calendar.WEEK_OF_YEAR, 2);
                    newExpense.setTime(c.getTime());
                    newExpense.setRecurrence(period);
                    FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
                }
                break;
            case 4: //every month
                for (i = 0; i < 11; i++) {
                    c.add(Calendar.MONTH, 1);
                    int currentDay = c.get(Calendar.DAY_OF_MONTH);
                    int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (max > currentDay && originalDayOfMonth > currentDay) {
                        currentDay = max > originalDayOfMonth ? originalDayOfMonth : max;
                        c.set(Calendar.DAY_OF_MONTH, currentDay);
                    }
                    newExpense.setTime(c.getTime());
                    newExpense.setRecurrence(period);
                    FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
                }
                break;
            case 5: //every two months
                for (i = 0; i < 5; i++) {
                    c.add(Calendar.MONTH, 2);
                    int currentDay = c.get(Calendar.DAY_OF_MONTH);
                    int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (max > currentDay && originalDayOfMonth > currentDay) {
                        currentDay = max > originalDayOfMonth ? originalDayOfMonth : max;
                        c.set(Calendar.DAY_OF_MONTH, currentDay);
                    }
                    newExpense.setTime(c.getTime());
                    newExpense.setRecurrence(period);
                    FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
                }
                break;
        }
        mActivity.tryReleaseTabs();
    }

    public void editExpense() {
        Expense newExpense = createExpenseFromFields();
        if (newExpense == null) {
            return;
        }

        newExpense.setId(expenseToShow.getId());
        FirebaseBackend.getInstance().editExpense(mActivity.mCurrentBudget.getId(), newExpense);

        mActivity.tryReleaseTabs();
    }

    private void handleRepeatPopup() {
        mRepeatAction.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        mRepeatAction.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_dark));

        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getActivity(), mRepeatAction);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.repeat_popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.no_repeat_opt:
                        mRepeatChoice = 0;
                        break;
                    case R.id.every_day_opt:
                        mRepeatChoice = 1;
                        break;
                    case R.id.every_week_opt:
                        mRepeatChoice = 2;
                        break;
                    case R.id.every_two_weeks_opt:
                        mRepeatChoice = 3;
                        break;
                    case R.id.every_month_opt:
                        mRepeatChoice = 4;
                        break;
                    case R.id.every_two_months_opt:
                        mRepeatChoice = 5;
                        break;
                }
                if (mRepeatChoice == 0) {
                    mRepeatText.setVisibility(View.GONE);
                }
                if (mRepeatChoice != 0) {
                    mRepeatText.setVisibility(View.VISIBLE);
                    mRepeatText.setText(item.getTitle());
                }
                return true;
            }
        });

        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                if (mRepeatChoice == 0) {
                    mRepeatAction.setColorFilter(ContextCompat.getColor(getContext(), R.color.accent_dark));
                    mRepeatAction.setBackgroundColor(Color.TRANSPARENT);
                    mRepeatText.setVisibility(View.GONE);
                }
            }
        });
        popup.show();//showing popup menu
    }

    private Expense createExpenseFromFields() {
        String title = mEditTextTitle.getText().toString();
        if (title.equals("")) {
            title = getResources().getString(R.string.general);
        }

        //  Handle is expense / income
        boolean isExpense = true;
        int selectedId = mRGIsExpense.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_income) {
            isExpense = false;
        }

        Category category = Category.getCategoryFromString(mCategorySpinner.getSelectedItem().toString());
        if (category == Category.CATEGORY) {
            Toast.makeText(mActivity, R.string.pick_category_toast, Toast.LENGTH_SHORT).show();
            return null;
        }

        Double amount;
        if (mEditTextAmount.getText().toString().equals("")) {
            amount = 0.0;
        } else {
            amount = Double.parseDouble(mEditTextAmount.getText().toString());
        }

        // Create expense
        Expense newExpense = new Expense("",
                amount,
                title,
                mEditTextDescription.getText().toString(),
                category,
                c.getTime(),
                expenseToShow == null ?
                        mActivity.mSessionManager.getUserId() :
                        expenseToShow.getUserID(),
                expenseToShow == null ?
                        mActivity.mSessionManager.getUserName() :
                        expenseToShow.getUserName(),
                isExpense);

        mActivity.mMonth = c.get(Calendar.MONTH);
        mActivity.mYear = c.get(Calendar.YEAR);
        return newExpense;
    }

    public void deleteExpense() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.confirm_delete);
        if (expenseToShow.isRecurrent())
        {
            builder.setMessage(R.string.delete_confirmation_expense_recurrent);
            builder.setNeutralButton(R.string.delete_recurring, new ExpenseFragment.DeleteDialogListener());
        }
        else
        {
            builder.setMessage(R.string.delete_confirmation_expense);
        }
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.yes, new ExpenseFragment.DeleteDialogListener());
        builder.setNegativeButton(R.string.no, new ExpenseFragment.DeleteDialogListener());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class DeleteDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                FirebaseBackend.getInstance().deleteExpense(mActivity.mCurrentBudget.getId(), expenseToShow.getId());
                Log.d("", "ExpenseFragment: deleting expense");
                Toast.makeText(mActivity, getResources().getString(R.string.expense_deleted), Toast.LENGTH_SHORT).show();
                // restart app, so won't go back to the deleted expense
                Intent restartIntent = mActivity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(mActivity.getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.mSessionManager.goToLastBudget();
                startActivity(restartIntent);
            }
            // should remove all future occurrenses.
            if (i == DialogInterface.BUTTON_NEUTRAL)
            {
                ArrayList<Expense> expensesInBudget = mActivity.mCurrentBudget.getExpenses();
                ArrayList<String> idsToDelete = new ArrayList<>();
                for (Expense currentExpense : expensesInBudget)
                {
                    long diff = currentExpense.getTime().getTime() - expenseToShow.getTime().getTime();
                    if(diff < 0)
                    {
                        continue;
                    }
                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    boolean auidEq = (currentExpense.getAlmostUniqueId() == expenseToShow.getAlmostUniqueId());
                    if(diffSeconds == 0 && diffMinutes == 0 && currentExpense.getTitle().equals(expenseToShow.getTitle()) && auidEq)
                    {
                        idsToDelete.add(currentExpense.getId());
                        Log.d("", String.format("adding %s", currentExpense.getId()));
                    }
                }
                for (String idToDelete : idsToDelete)
                {
                    FirebaseBackend.getInstance().deleteExpense(mActivity.mCurrentBudget.getId(), idToDelete);
                    Log.d("", String.format("deleting %s", idToDelete));
                }

                Log.d("", "ExpenseFragment: deleting expense");
                Toast.makeText(mActivity, getResources().getString(R.string.expense_deleted), Toast.LENGTH_SHORT).show();
                // restart app, so won't go back to the deleted expense
                Intent restartIntent = mActivity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(mActivity.getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.mSessionManager.goToLastBudget();
                startActivity(restartIntent);
            }
        }
    }
}