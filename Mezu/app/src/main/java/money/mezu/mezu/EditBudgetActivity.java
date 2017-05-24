package money.mezu.mezu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EditBudgetActivity extends BaseNavDrawerActivity {

    protected Budget mCurrentBudget;
    List<String> partnersEmails;
    TextView partnersList;
    AutoCompleteTextView addPartnerEmailTextView;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        Gson gson = new Gson();
        String json = getIntent().getStringExtra("budget");
        mCurrentBudget = gson.fromJson(json, Budget.class);

        EditText budgetNameEditText = (EditText)findViewById(R.id.budget_name);
        budgetNameEditText.setText(mCurrentBudget.getName());

        double curBalance = mCurrentBudget.getInitialBalance();
        if (curBalance != 0){
            EditText initialBalanceEditText = (EditText)findViewById(R.id.starting_balance);
            initialBalanceEditText.setText(String.valueOf(curBalance));
        }

        partnersEmails = new ArrayList<>();
        partnersList = (TextView)findViewById(R.id.partners_list);
        addPartnerEmailTextView = (AutoCompleteTextView)findViewById(R.id.partner_email);

        tryInitializingContactEmails();

        Button saveBudgetBtn = (Button) findViewById(R.id.save_budget);
        saveBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetName = ((EditText) findViewById(R.id.budget_name)).getText().toString();
                String startingBalanceString = ((EditText) findViewById(R.id.starting_balance)).getText().toString();
                double startingBalance;
                if (startingBalanceString.equals("")) {
                    startingBalance = 0; // By convention
                } else {
                    startingBalance = Double.parseDouble(startingBalanceString);
                }
                if (!(budgetName.equals(mCurrentBudget.getName()) &&
                        startingBalance == mCurrentBudget.getInitialBalance())) { // Something changed
                    mCurrentBudget.setName(budgetName);
                    mCurrentBudget.setInitialBalance(startingBalance);
                    FirebaseBackend.getInstance().editBudget(mCurrentBudget);
                }
                for (String email : partnersEmails) {
                    FirebaseBackend.getInstance().connectBudgetAndUserByEmail(mCurrentBudget, email);
                }
                BudgetViewActivity.goToBudgetView(EditBudgetActivity.this, mCurrentBudget, mSessionManager);
            }
        });

        Button addPartnerBtn = (Button) findViewById(R.id.add_partner);
        addPartnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText partnerEmailView = (EditText)findViewById(R.id.partner_email);
                String partnerEmail = partnerEmailView.getText().toString();
                if (partnerEmail.equals("")) {
                    Toast.makeText(EditBudgetActivity.this, "Partner's email is empty!", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(partnerEmail)){
                    Toast.makeText(EditBudgetActivity.this, "Email is not valid!", Toast.LENGTH_SHORT).show();
                } else { // email is valid
                    partnersEmails.add(partnerEmail);
                    String emailsList = partnersEmails.toString();
                    partnersList.setText(emailsList.substring(1,emailsList.length()-1));// to delete brackets
                    partnerEmailView.setText("");
                    findViewById(R.id.add_budget_layout).invalidate();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.budget_edit_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_delete)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.confirm_delete);
            builder.setMessage(R.string.delete_confirmation_text);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.yes, new MyDialogListener());
            builder.setNegativeButton(R.string.no, new MyDialogListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    private class MyDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(i==DialogInterface.BUTTON_POSITIVE) {
                FirebaseBackend.getInstance().leaveBudget(mCurrentBudget.getId(), mSessionManager.getUserId());
                Toast.makeText(EditBudgetActivity.this, "Budget deleted", Toast.LENGTH_SHORT).show();
                // restart app, so won't go back to the deleted budget
                Intent restartIntent = EditBudgetActivity.this.getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(EditBudgetActivity.this.getPackageName() );
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mSessionManager.setLastBudget((String)null);
                startActivity(restartIntent);
            }
        }
    }

    private boolean isValidEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void tryInitializingContactEmails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            new InitContactEmailsTask().execute(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new InitContactEmailsTask().execute(this);
            } else {
                //Toast.makeText(this, "Until you grant the permission, there will be no autocomplete for emails", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class InitContactEmailsTask extends AsyncTask<Activity, Void, Activity> {
        private ArrayList<String> contactEmails;

        @Override
        protected Activity doInBackground(Activity... activities) {
            initializeContactEmails();
            return activities[0];
        }

        private void initializeContactEmails() {
            contactEmails = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    contactEmails.add(email);
                }
                emailCur.close();
            }
            cursor.close();
        }

        protected void onPostExecute(Activity activity) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                    android.R.layout.simple_dropdown_item_1line, contactEmails);
            addPartnerEmailTextView.setAdapter(adapter);
        }
    }
}
