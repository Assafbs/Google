package money.mezu.mezu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class ExpenseFragment extends Fragment {
    private View mView;

    EditText mEditTextAmount;
    EditText mEditTextTitle;
    Spinner mCategorySpinner;
    EditText mEditTextDate;
    EditText mEditTextTime;
    EditText mEditTextDescription;
    RadioGroup mRGIsExpense;
    Button mAddButton;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private Calendar c;

    private boolean incomeSelected = false;
    private Category incomeCat = Category.CATEGORY;
    private Category expenseCat = Category.CATEGORY;
    private ArrayAdapter<String> incomeAdapter;
    private ArrayAdapter<String> expenseAdapter;

    private Expense expenseToShow;

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
        TextView titleView = (TextView) mView.findViewById(R.id.add_expense_title);
        LinearLayout linearLayoutEdit = (LinearLayout) mView.findViewById(R.id.edit_expense_layout);
        ImageView deleteBtn = (ImageView) mView.findViewById(R.id.delete_expense);
        if (titleString == null) {
            titleString = "General";
        }
        titleView.setText(titleString);
        titleView.setVisibility(View.VISIBLE);
        linearLayoutEdit.setVisibility(View.VISIBLE);
        linearLayoutEdit.setClickable(true);
        deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.bringToFront();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "ExpenseFragment: clicked delete expense");
                deleteExpense();
            }
        });

        if (expenseToShow.getAmount() == 0.0) {
            mEditTextAmount.setText("0.0");
        } else {
            mEditTextAmount.setText("" + expenseToShow.getAmount());
        }

        ArrayList<String> categories = new ArrayList<>();
        categories.add(expenseToShow.getCategory().toString());
        mCategorySpinner.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.category_spinner_item, categories));

        RadioButton rb_expense = (RadioButton) mView.findViewById(R.id.radio_expense);
        RadioButton rb_income = (RadioButton) mView.findViewById(R.id.radio_income);
        rb_expense.setClickable(false);
        rb_income.setClickable(false);

        if (expenseToShow.getIsExpense()) {
            rb_expense.setChecked(true);
            rb_income.setChecked(false);
        } else {
            rb_expense.setChecked(false);
            rb_income.setChecked(true);
        }

        mEditTextTitle.setText("Added by: " + expenseToShow.getUserName());
        mEditTextTitle.setHint("");

        mEditTextDescription.setText(expenseToShow.getDescription());
        mEditTextDescription.setHint("");

        mEditTextDate.setText(DateFormat.getDateInstance().format(expenseToShow.getTime()));
        mEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(expenseToShow.getTime()));

        ViewGroup viewGroup = (ViewGroup) mView.findViewById(R.id.activity_add_expense);
        disableAllFields(viewGroup);

        mAddButton.setVisibility(View.INVISIBLE);
    }

    private void disableAllFields(ViewGroup viewGroup) {
        for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup)
                disableAllFields((ViewGroup) child);
            if (child != null) {
                if (child.getId() != R.id.delete_expense) {
                    child.setEnabled(false);
                }
            }
        }
    }

    private void setupAddExpense() {
        mEditTextAmount.post(new Runnable() {
            public void run() {
                mEditTextAmount.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(mEditTextAmount, 0);
            }
        });

        // Get Current Time
        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Set Current Time to EditTexts
        mEditTextDate.setText(DateFormat.getDateInstance().format(c.getTime()));
        mEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));

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

        expenseAdapter = new ArrayAdapter<String>(mActivity, R.layout.category_spinner_item, Category.getExpenseCategoriesList());
        incomeAdapter = new ArrayAdapter<String>(mActivity, R.layout.category_spinner_item, Category.getIncomeCategoriesList());
        mCategorySpinner.setAdapter(expenseAdapter);

        mRGIsExpense.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.radio_income & !incomeSelected) {
                    expenseCat = Category.getCategoryFromString(mCategorySpinner.getSelectedItem().toString());
                    mCategorySpinner.setAdapter(incomeAdapter);
                    mCategorySpinner.setSelection(incomeCat.getSpinnerLocation(true));
                    incomeSelected = true;
                } else {
                    incomeCat = Category.getCategoryFromString(mCategorySpinner.getSelectedItem().toString());
                    mCategorySpinner.setAdapter(expenseAdapter);
                    mCategorySpinner.setSelection(expenseCat.getSpinnerLocation(false));
                    incomeSelected = false;
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                addExpense();
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
                            mEditTextTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime()));
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
            return true;
        }
        return false;
    }

    public void addExpense() {
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
            Toast.makeText(mActivity, "Pick a category", Toast.LENGTH_SHORT).show();
            return;
        }

        Double amount;
        if (mEditTextAmount.getText().toString().equals("")) {
            amount = 0.0;
        } else {
            amount = Double.parseDouble(mEditTextAmount.getText().toString());
        }
        //CREATE EXPENSE
        Expense newExpense = new Expense("",
                amount,
                title,
                mEditTextDescription.getText().toString(),
                category,
                c.getTime(),
                mActivity.mSessionManager.getUserId(),
                mActivity.mSessionManager.getUserName(),
                isExpense);

        FirebaseBackend.getInstance().addExpenseToBudget(mActivity.mCurrentBudget, newExpense);
        mActivity.tryReleaseTabs();
    }

    public void deleteExpense() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.delete_confirmation_expense);
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
                Toast.makeText(mActivity, "Expense deleted", Toast.LENGTH_SHORT).show();
                // restart app, so won't go back to the deleted expense
                Intent restartIntent = mActivity.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(mActivity.getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartIntent);
            }
        }
    }
}
