package money.mezu.mezu;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;



public class ExpenseAdapter extends ArrayAdapter<Expense> {
    Context mContext;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        super(context, 0, expenses);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Expense expense = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_expense, parent, false);
        }
        // Lookup view for data population
        TextView category = (TextView) convertView.findViewById(R.id.expenseCategory);
        TextView amount = (TextView) convertView.findViewById(R.id.expenseAmount);
        TextView title = (TextView) convertView.findViewById(R.id.expenseTitle);
        LinearLayout expenseRow = (LinearLayout) convertView.findViewById(R.id.expenseRow);
        expenseRow.setTag(expense);

        // Populate the data into the template view using the data object

        //BAHH
        if (expense.getCategory() != null) {
            category.setText(expense.getCategory().toString());
        } else {
            category.setText(R.string.category_other);
        }
        amount.setText(Double.toString(expense.getAmount()));
        if (!expense.getIsExpense()) {
            amount.setText(Double.toString(expense.getAmount()) + "+");
            amount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
        String t_title = expense.getTitle();
        if (t_title == null) {
            title.setText(R.string.general);
        } else {
            title.setText(expense.getTitle());
        }

        expenseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Expense expense = (Expense) view.getTag();
                showPopup(mContext, expense);
                ((Activity) mContext).findViewById(R.id.fab_expense).setVisibility(View.INVISIBLE);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private void showPopup(final Context context, Expense expense) {
        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.activity_add_expense, null);
        LinearLayout viewGroup = (LinearLayout) layout.findViewById(R.id.activity_add_expense);

        // Creating the PopupWindow
        final PopupWindow popUp = new PopupWindow(context);
        popUp.setContentView(layout);
        popUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        popUp.setFocusable(true);

        String titleString = expense.getTitle();
        TextView titleView = (TextView) layout.findViewById(R.id.add_expense_title);
        if (titleString == null) {
            titleString = "General";
        }
        titleView.setText(titleString);
        titleView.setVisibility(View.VISIBLE);

        EditText amount = (EditText) layout.findViewById(R.id.EditTextAmount);
        if (expense.getAmount() == 0.0) {
            amount.setText("0.0");
        } else {
            amount.setText("" + expense.getAmount());
        }

        Spinner category = (Spinner) layout.findViewById(R.id.SpinnerCategoriesType);
        category.setSelection(expense.getCategory().getValue());

        RadioButton rb_expense = (RadioButton) layout.findViewById(R.id.radio_expense);
        RadioButton rb_income = (RadioButton) layout.findViewById(R.id.radio_income);
        rb_expense.setClickable(false);
        rb_income.setClickable(false);

        if (expense.getIsExpense()) {
            rb_expense.setChecked(true);
            rb_income.setChecked(false);
        } else {
            rb_expense.setChecked(false);
            rb_income.setChecked(true);
        }

        EditText user = (EditText) layout.findViewById(R.id.EditTextTitle);
        user.setText(expense.getUserName());
        user.setHint("");


        EditText description = (EditText) layout.findViewById(R.id.EditTextDescription);
        description.setText(expense.getDescription());
        description.setHint("");


        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(false);
        }

        EditText dateField = (EditText) layout.findViewById(R.id.EditTextDate);
        EditText timeField = (EditText) layout.findViewById(R.id.EditTextTime);
        dateField.setText(DateFormat.getDateInstance().format(expense.getTime()));
        timeField.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(expense.getTime()));

        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ((Activity) context).findViewById(R.id.fab_expense).setVisibility(View.VISIBLE);
            }
        });

        popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
        Button add_btn = (Button) layout.findViewById(R.id.add_action_btn);
        add_btn.setVisibility(View.INVISIBLE);

    }
}
