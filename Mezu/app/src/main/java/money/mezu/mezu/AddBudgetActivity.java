package money.mezu.mezu;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AddBudgetActivity extends Activity {

    List<String> partnersEmails;
    TextView partnersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        partnersEmails = new ArrayList<String>();
        partnersList = (TextView)findViewById(R.id.partners_list);

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
                    finish();
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
}
