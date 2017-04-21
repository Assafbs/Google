package money.mezu.mezu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity {

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);

        Button startBtn = (Button)findViewById(R.id.start_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: check if user is logged in already
                sessionManager.createLoginSession("dummy", new UserIdentifier(0));
                Intent budgetsIntent = new Intent(LoginActivity.this,BudgetsActivity.class);
                startActivity(budgetsIntent);
            }
        });
    }
}
