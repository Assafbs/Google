package money.mezu.mezu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseNavDrawerActivity extends AppCompatActivity implements View.OnClickListener, BudgetUpdatedListener, UserLeftBudgetListener, LocalCacheReadyListener{

    private String mDrawerName;
    private String mDrawerEmail;
    private Uri mDrawerImage;
    protected Toolbar mToolbar;

    View mDrawerView;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    protected HashMap<String, Budget> mapOfBudgets = new HashMap<String, Budget> ();


    protected GoogleApiClient mGoogleApiClient;
    protected SessionManager mSessionManager = null;

    protected void onCreateDrawer()
    {
        Log.d("","BaseNavDrawerActivity::onCreateDrawer start");
        instantiateSessionManager();
        mDrawerName = mSessionManager.getUserName();
        mDrawerEmail = mSessionManager.getUserEmail();
        mDrawerImage = mSessionManager.getUserImage();

        /* Assinging the toolbar object ot the view and setting the the Action bar to our toolbar*/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Following code is to make sure labels are aligned with language chosen
        String label = null;
        try {
            label = getResources().getString(
                    getPackageManager().getActivityInfo(getComponentName(), 0).labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!label.equals(getString(R.string.title_activity_budget_view))){
            getSupportActionBar().setTitle(label);
        }

        ViewStub navDrawerStub = (ViewStub) findViewById(R.id.nav_drawer_stub);
        mDrawerView = navDrawerStub.inflate();

        setupHeader();
        setupButtons();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0); // this disables the animation
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        EventDispatcher.getInstance().registerBudgetUpdateListener(this);
        this.mapOfBudgets = BackendCache.getInstance().getBudgets();
        ListView listView = (ListView) mDrawerView.findViewById(R.id.budgets_list);
        BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
        listView.setAdapter(adapter);
        EventDispatcher.getInstance().registerUserLeftBudgetListener(this);
        EventDispatcher.getInstance().registerLocalCacheReadyListener(this);
    }
    //************************************************************************************************************************************************
    private void setupHeader() {
        CircleImageView image = (CircleImageView) mDrawerView.findViewById(R.id.circleView);
        TextView name = (TextView) mDrawerView.findViewById(R.id.name);
        TextView email = (TextView) mDrawerView.findViewById(R.id.email);


        if (mDrawerImage != null) {
            Picasso.with(StaticContext.mContext).load(mDrawerImage).into(image);
        }
        name.setText(mDrawerName);
        email.setText(mDrawerEmail);
    }
    //************************************************************************************************************************************************
    private void setupButtons() {
        View buttons = mDrawerView.findViewById(R.id.nav_drawer_bottom_options);
        buttons.findViewById(R.id.add_budget).setOnClickListener(this);
        buttons.findViewById(R.id.logout).setOnClickListener(this);
        buttons.findViewById(R.id.settings).setOnClickListener(this);
    }
    //************************************************************************************************************************************************
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                logout();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.settings:
                openSettings();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.add_budget:
                addBudget();
                mDrawerLayout.closeDrawers();
                break;
        }
    }
    //************************************************************************************************************************************************
    private void instantiateSessionManager()
    {
        if (mSessionManager == null){
            mSessionManager = new SessionManager(this);
        }
    }

    //************************************************************************************************************************************************
    // This function is here so when any extending class will call setContentView the drawer will be created
    @Override
    public void setContentView(@LayoutRes int layoutResID)
    {
        super.setContentView(layoutResID);
        onCreateDrawer();
    }
    //************************************************************************************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //************************************************************************************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Currently no option items
        return super.onOptionsItemSelected(item);
    }
    //************************************************************************************************************************************************
    protected void logout()
    {
        if (mSessionManager.getLoginType().equals("Google")) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                        }
                    });
        }
        mSessionManager.logoutUser();
    }
    //************************************************************************************************************************************************
    protected void openSettings() {
        Intent SettingsIntent = new Intent(BaseNavDrawerActivity.this, SettingsActivity.class);
        startActivity(SettingsIntent);
    }
    //************************************************************************************************************************************************
    protected void addBudget() {
        Intent addBudgetIntent = new Intent(BaseNavDrawerActivity.this, AddBudgetActivity.class);
        startActivity(addBudgetIntent);
    }
    //************************************************************************************************************************************************
    public void budgetUpdatedCallback(Budget budget)
    {
        Log.d("",String.format("BaseNavDrawerActivity:budgetUpdatedCallback: invoked with budget: %s", budget.toString()));
        if (mapOfBudgets.containsKey(budget.getId()))
        {
            boolean expensesDiffer = budget.expensesDiffer(mapOfBudgets.get(budget.getId()));
            mapOfBudgets.get(budget.getId()).setFromBudget(budget);
            if(expensesDiffer)
            {
                EventDispatcher.getInstance().notifyExpenseUpdatedListeners();
            }
        }
        else
        {
            this.mapOfBudgets.put(budget.getId(), budget);
        }
        ListView listView = (ListView) mDrawerView.findViewById(R.id.budgets_list);
        BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
        listView.setAdapter(adapter);
    }
    //************************************************************************************************************************************************
    @Override
    public void localCacheReadyCallback() {
        BudgetsActivity.budgetsLoadedFromDB = true;
    }
    //************************************************************************************************************************************************
    public void userLeftBudgetCallback(String bid)
    {
        Log.d("",String.format("BackendCache:userLeftBudgetCallback: invoked with bid: %s", bid));
        if (this.mapOfBudgets.containsKey(bid))
        {
            this.mapOfBudgets.remove(bid);
            ListView listView = (ListView) mDrawerView.findViewById(R.id.budgets_list);
            BudgetAdapter adapter = new BudgetAdapter(this, new ArrayList<Budget>(this.mapOfBudgets.values()));
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed(){
        if (this.isTaskRoot()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.on_exit_by_back)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            closeApp();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void closeApp(){
        super.onBackPressed();
    }

}
