package money.mezu.mezu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public abstract class BaseNavDrawerActivity extends AppCompatActivity {

    private String mDrawerTitles[] = {"Home","Events","Mail","Shop","Travel"};
    private int mDrawerIcons[] = {R.mipmap.budget_icon,
            R.mipmap.logout_icon,
            R.mipmap.add_budget_icon,
            R.mipmap.menu_icon,
            R.mipmap.nav_drawer_icon};

    private String mDrawerName;
    private String mDrawerEmail;
    private Uri mDrawerImage;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    protected GoogleApiClient mGoogleApiClient;
    protected SessionManager mSessionManager = null;
    protected BackendInterface mBackend = FirebaseBackend.getInstance();

    protected void onCreateDrawer() {
        instansiateSessionManager();
        mDrawerName = mSessionManager.getUserName();
        mDrawerEmail = mSessionManager.getUserEmail();
        mDrawerImage = mSessionManager.getUserImage();

        /* Assinging the toolbar object ot the view and setting the the Action bar to our toolbar*/
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new NavDrawerAdapter(mDrawerTitles, mDrawerIcons, mDrawerName, mDrawerEmail, mDrawerImage);

        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.openDrawer,R.string.closeDrawer){

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
    }
    //************************************************************************************************************************************************
    private void instansiateSessionManager()
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
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent SettingsIntent = new Intent(BaseNavDrawerActivity.this, SettingsActivity.class);
            startActivity(SettingsIntent);
        }
        else if (id == R.id.action_log_out)
        {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    //************************************************************************************************************************************************
    private void logout()
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
}
