package com.lipi.notifica;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.lipi.notifica.database.Client;
import com.lipi.notifica.database.DbHelper;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private RoutineFragment mRoutineFragment;
    private NewsFeedFragment mNewsFeedFragment;

    private ActionBarDrawerToggle mDrawerToggle;

    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener;

    public void initDb() {
        // Download and get new routine
        Client client = new Client(this);
        client.getRoutine(new Client.ClientListener() {
            // The refresh is called number of times as new data are downloaded.
            // This.queue represents number of refresh callbacks that are pending.
            // When it is zero, it means everything is downloaded completely.
            @Override
            public void refresh() {
                Log.d("refreshing routine", "queue size: " + this.queue.size());

                // Clean up unnecessary cache data
                DbHelper dbHelper = new DbHelper(MainActivity.this);
                dbHelper.clean();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDb();

        // Create the fragment once
        mRoutineFragment = new RoutineFragment();
        mNewsFeedFragment = new NewsFeedFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        mNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.news_feed:
                        selectedFragment = mNewsFeedFragment;
                        break;
                    case R.id.routine:
                        selectedFragment = mRoutineFragment;
                        break;
                    case R.id.assignment:
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        return false;
                }

                if( selectedFragment != null ){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, selectedFragment).commit();
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        };
        navigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.openDrawer, R.string.closeDrawer);

        // Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Sync-ing is necessary to show hamburger icon
        mDrawerToggle.syncState();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Start from new feed page by default
        int startPage = R.id.news_feed;
        // Start from intended page if passed through intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            startPage = extras.getInt("start_page", R.id.news_feed);
        }

        mNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(startPage));

    }
}
