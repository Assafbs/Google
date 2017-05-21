package money.mezu.mezu;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

import android.os.Build;

import com.google.gson.Gson;

public class AddBudgetActivity extends BaseNavDrawerActivity {

    List<String> partnersEmails;
    TextView partnersList;
    AutoCompleteTextView addPartnerEmailTextView;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        partnersEmails = new ArrayList<>();
        partnersList = (TextView)findViewById(R.id.partners_list);
        addPartnerEmailTextView = (AutoCompleteTextView)findViewById(R.id.partner_email);

        tryInitializingContactEmails();

        Button addBudgetBtn = (Button) findViewById(R.id.add_budget);
        addBudgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String BudgetName = ((EditText) findViewById(R.id.budget_name)).getText().toString();
                String startingBalanceString = ((EditText) findViewById(R.id.starting_balance)).getText().toString();
                double startingBalance;
                if (startingBalanceString.equals("")) {
                    startingBalance = 0; // By convention
                } else {
                    startingBalance = Double.parseDouble(startingBalanceString);
                }

                if (BudgetName.equals("")) {
                    Toast.makeText(AddBudgetActivity.this, "Must provide budget name!", Toast.LENGTH_SHORT).show();
                } else if (false) { //TODO: replace with check that budget name is valid (change toast text accordingly)
                    Toast.makeText(AddBudgetActivity.this, "Please choose a different budget name!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("","AddBudgetActivity: adding budget to db");
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    UserIdentifier uid = sessionManager.getUserId();
                    Budget newBudget = new Budget(BudgetName, startingBalance);
                    FirebaseBackend.getInstance().createBudgetAndAddToUser(newBudget, uid);
                    for (String email : partnersEmails) {
                        FirebaseBackend.getInstance().connectBudgetAndUserByEmail(newBudget, email);
                    }
                    BudgetViewActivity.goToBudgetView(AddBudgetActivity.this, newBudget, mSessionManager);
                }
            }
        });

        Button addPartnerBtn = (Button) findViewById(R.id.add_partner);
        addPartnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText partnerEmailView = (EditText)findViewById(R.id.partner_email);
                String partnerEmail = partnerEmailView.getText().toString();
                if (partnerEmail.equals("")) {
                    Toast.makeText(AddBudgetActivity.this, "Partner's email is empty!", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(partnerEmail)){
                    Toast.makeText(AddBudgetActivity.this, "Email is not valid!", Toast.LENGTH_SHORT).show();
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

    private boolean isValidEmail(String email){
        //TODO: validate email; if function is implemented somewhere else, use same.
        //TODO: maybe check if it's in the system, and if not send and invitation to Mezu?
        return true;
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
