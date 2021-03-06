package money.mezu.mezu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

public class AddBudgetActivity extends BaseNavDrawerActivity {

    static boolean show_permissions_dialog = true;

    ArrayList<String> partnersEmails;
    FlowLayout partnersChipsContainer;
    AutoCompleteTextView addPartnerEmailTextView;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        partnersEmails = new ArrayList<>();
        partnersChipsContainer = (FlowLayout)findViewById(R.id.partners_chips_container);
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
                    Toast.makeText(AddBudgetActivity.this, R.string.must_provide_budget_name, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("", "AddBudgetActivity: adding budget to db");
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    UserIdentifier uid = sessionManager.getUserId();
                    ArrayList<String> myEmail = new ArrayList<>();
                    myEmail.add(sessionManager.getUserEmail().toLowerCase());
                    Budget newBudget = new Budget(BudgetName, startingBalance, myEmail, uid.getId().toString(), partnersEmails);
                    FirebaseBackend.getInstance().createBudgetAndAddToUser(newBudget, uid);
                    BudgetViewActivity.goToBudgetView(AddBudgetActivity.this, newBudget, mSessionManager);
                }
            }
        });

        Button addPartnerBtn = (Button) findViewById(R.id.add_partner);
        addPartnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText partnerEmailView = (EditText) findViewById(R.id.partner_email);
                String partnerEmail = partnerEmailView.getText().toString().toLowerCase();
                if (partnerEmail.equals("")) {
                    Toast.makeText(AddBudgetActivity.this, R.string.partner_email_is_empty, Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(partnerEmail)) {
                    Toast.makeText(AddBudgetActivity.this, R.string.email_not_valid, Toast.LENGTH_SHORT).show();
                } else if (partnersEmails.contains(partnerEmail)) {
                    Toast.makeText(AddBudgetActivity.this, R.string.email_already_in_list, Toast.LENGTH_SHORT).show();
                } else { // email is valid
                    partnersEmails.add(partnerEmail);
                    final Chip chip = new Chip(AddBudgetActivity.this);
                    chip.setChipText(partnerEmail);
                    chip.setClosable(true);
                    chip.setOnCloseClickListener(new OnCloseClickListener() {
                        @Override
                        public void onCloseClick(View v) {
                            Chip c = (Chip) v.getParent();
                            partnersEmails.remove(c.getChipText());
                            partnersChipsContainer.removeView(c);
                        }
                    });
                    chip.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    partnersChipsContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            partnersChipsContainer.addView(chip);
                        }
                    });
                    partnerEmailView.setText("");
                    findViewById(R.id.starting_balance).requestFocus();
                    mDrawerLayout.invalidate();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void tryInitializingContactEmails() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (show_permissions_dialog) {
                showPermissionsDialog(); // explain why we ask for the permissions
                show_permissions_dialog = false; // but do it only once
            }
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            new InitContactEmailsTask().execute(this);
        }
    }

    private void showPermissionsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(AddBudgetActivity.this).create();
        alertDialog.setTitle(R.string.contacts_permissions);
        alertDialog.setMessage(getResources().getString(R.string.contacts_permissions_explanation));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.got_it),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                new InitContactEmailsTask().execute(this);
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
            assert cursor != null;
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                assert emailCur != null;
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
