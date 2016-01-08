package com.lipi.notifica;

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
import android.view.View;
import android.widget.Toast;

import com.lipi.notifica.database.Client;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private RoutineFragment mRoutineFragment;
    private NewsfeedFragment mNewsfeedFragment;

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Download and get new routine
        Client client = new Client(this);
        client.getRoutine(new Client.ClientListener() {
            // The refresh is called number of times as new data are downloaded.
            // This.queue represents number of refresh callbacks that are pending.
            // When it is zero, it means everything is downloaded completely.
            @Override
            public void refresh() {
                Log.d("refreshing routine", "queue size: "+this.queue.size());
            }
        });

        // Create the fragment once
        mRoutineFragment = new RoutineFragment();
        mNewsfeedFragment = new NewsfeedFragment();

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.news_feed:
                        selectedFragment = mNewsfeedFragment;
                        break;
                    case R.id.routine:
                        selectedFragment = mRoutineFragment;
                        break;
                    case R.id.assignment:
                        break;
                    case R.id.settings:
                        break;
                    default:
                        break;
                }
                if( selectedFragment != null ){
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame_content, selectedFragment);
                    fragmentTransaction.commit();
                    if(menuItem.isChecked()) menuItem.setChecked(false);
                    else menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                }
                return true;
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer);

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // start up app with News feed
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, mNewsfeedFragment);
            fragmentTransaction.commit();
            navigationView.getMenu().findItem(R.id.news_feed).setChecked(true);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
