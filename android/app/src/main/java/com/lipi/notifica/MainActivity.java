package com.lipi.notifica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lipi.notifica.database.Client;
import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Profile;
import com.lipi.notifica.database.User;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private RoutineFragment mRoutineFragment;
    private NewsFeedFragment mNewsFeedFragment;

    private ActionBarDrawerToggle mDrawerToggle;

    boolean isVisible = true;
    boolean swap = true;

    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener;

    public void initDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Download and get new routine
                Client client = new Client(MainActivity.this);
                client.getRoutine(new Client.ClientListener() {
                    // The refresh is called number of times as new data are downloaded.
                    // This.queue represents number of refresh callbacks that are pending.
                    // When it is zero, it means everything is downloaded completely.
                    @Override
                    public void refresh() {
                        // Log.d("refreshing routine", "queue size: " + this.queue.size());

                        if (this.queue.size() == 0) {
                            // Clean up unnecessary cache data
                            DbHelper dbHelper = new DbHelper(MainActivity.this);
                            dbHelper.clean();
                        }
                    }
                });
            }
        }).run();
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

        // Set from profile
        DbHelper helper = new DbHelper(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        User user = User.get(User.class, helper, "username=?", new String[]{preferences.getString("username", "")}, null);
        Profile profile = Profile.get(Profile.class, helper, user.profile);

        final View headerView = navigationView.getHeaderView(0);
        String name = user.first_name + " " + user.last_name;
        ((TextView)headerView.findViewById(R.id.username)).setText(name);
        ((TextView)headerView.findViewById(R.id.email)).setText(user.email);
        ((ImageView)headerView.findViewById(R.id.avatar)).setImageBitmap(profile.getAvatar());

        ((GradientDrawable)(headerView.findViewById(R.id.avatar)).getBackground()).setColor(0xFFFFFFFF);

        final Menu defaultMenu = navigationView.getMenu();

        defaultMenu.setGroupCheckable(R.id.basic_group, true, true);
        defaultMenu.setGroupCheckable(R.id.settings_group, true, true);

        MenuItem newsFeedItem = defaultMenu.add(R.id.basic_group, R.id.news_feed, Menu.NONE, "News Feed");
        newsFeedItem.setIcon(R.mipmap.news_feed);
        newsFeedItem.setCheckable(true);
        newsFeedItem.setChecked(false);

        MenuItem routineItem = defaultMenu.add(R.id.basic_group, R.id.routine, Menu.NONE, "Routine");
        routineItem.setIcon(R.mipmap.routine);
        routineItem.setCheckable(true);
        routineItem.setChecked(false);

        MenuItem assignmentItem = defaultMenu.add(R.id.basic_group, R.id.assignment, Menu.NONE, "Assignments");
        assignmentItem.setIcon(R.mipmap.assignment);
        assignmentItem.setCheckable(true);
        assignmentItem.setChecked(false);

        MenuItem settingsItem = defaultMenu.add(R.id.settings_group, R.id.settings, Menu.NONE, "Settings");
        settingsItem.setIcon(R.mipmap.settings);
        settingsItem.setCheckable(true);
        settingsItem.setChecked(false);

        MenuItem addClass = defaultMenu.add(R.id.classes_group, R.id.add_class, Menu.NONE,"Add Class");
        defaultMenu.setGroupVisible(R.id.classes_group, !isVisible);
        addClass.setIcon(R.mipmap.ic_launcher);
        addClass.setCheckable(true);
        addClass.setChecked(false);

        final ImageButton swapClasses = (ImageButton) headerView.findViewById(R.id.class_select);
        swapClasses.setBackgroundResource(R.mipmap.swap_class);

        swapClasses.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isVisible=!isVisible;
                if(swap){
                    swapClasses.setBackgroundResource(R.mipmap.close);
                    swap = false;
                }
                else{
                    swapClasses.setBackgroundResource(R.mipmap.swap_class);
                    swap = true;
                }
                defaultMenu.setGroupVisible(R.id.basic_group,isVisible);
                defaultMenu.setGroupVisible(R.id.settings_group,isVisible);
                defaultMenu.setGroupVisible(R.id.classes_group,!isVisible);
            }
        });
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
                        startActivityForResult(intent, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Settings activity telling us to close
        if(requestCode == 1 && resultCode == -1)
            finish();
    }
}

