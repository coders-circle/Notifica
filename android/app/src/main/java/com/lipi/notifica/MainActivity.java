package com.lipi.notifica;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        // Clean up unnecessary cache data
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        dbHelper.clean();

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
                    }
                });
            }
        }).run();
    }

    // Add a menu item to a group with given id, name and icon
    private MenuItem addMenuItem(Menu menu, int group, int id, String name, int icon) {
        MenuItem menuItem = menu.add(group, id, Menu.NONE, name);
        menuItem.setIcon(icon);
        menuItem.setCheckable(true);
        menuItem.setChecked(false);
        return menuItem;
    }

    // Prepare the navigation drawer menu
    private void prepareMenu() {
        // Add checkable groups to the menu
        final Menu defaultMenu = navigationView.getMenu();
        defaultMenu.setGroupCheckable(R.id.basic_group, true, true);
        defaultMenu.setGroupCheckable(R.id.settings_group, true, true);

        // Add menu items
        addMenuItem(defaultMenu, R.id.basic_group, R.id.news_feed, "News Feed", R.mipmap.news_feed);
        addMenuItem(defaultMenu, R.id.basic_group, R.id.routine, "Routine", R.mipmap.routine);
        addMenuItem(defaultMenu, R.id.basic_group, R.id.assignment, "Assignments", R.mipmap.assignment);
        addMenuItem(defaultMenu, R.id.settings_group, R.id.settings, "Settings", R.mipmap.settings);
        addMenuItem(defaultMenu, R.id.classes_group, R.id.add_class, "Add Class", R.mipmap.ic_launcher);

        defaultMenu.setGroupVisible(R.id.classes_group, !isVisible);

        // Initialize the swap button in the drawer
        View headerView = navigationView.getHeaderView(0);
        final ImageView swapClasses = (ImageView) headerView.findViewById(R.id.class_select);
        swapClasses.setImageResource(R.mipmap.swap_class);
        ((View)swapClasses.getParent()).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isVisible = !isVisible;
                if (swap) {
                    swapClasses.setImageResource(R.mipmap.close);
                    swap = false;
                } else {
                    swapClasses.setImageResource(R.mipmap.swap_class);
                    swap = true;
                }
                defaultMenu.setGroupVisible(R.id.basic_group, isVisible);
                defaultMenu.setGroupVisible(R.id.settings_group, isVisible);
                defaultMenu.setGroupVisible(R.id.classes_group, !isVisible);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDb();

        // Create the fragments once
        mRoutineFragment = new RoutineFragment();
        mNewsFeedFragment = new NewsFeedFragment();

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Initialize the navigation drawer

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        // Set the drawer header contents from the user profile
        User user = User.getLoggedInUser(this);
        Profile profile = Profile.get(Profile.class, new DbHelper(this), user.profile);
        ((TextView)headerView.findViewById(R.id.username)).setText(user.getName());
        ((TextView)headerView.findViewById(R.id.email)).setText(user.email);
        ((ImageView)headerView.findViewById(R.id.avatar)).setImageBitmap(profile.getAvatar());
        ((GradientDrawable)(headerView.findViewById(R.id.avatar)).getBackground()).setColor(0xFFFFFFFF);

        // Prepare the menu items on the drawer
        prepareMenu();

        // Handle navigation item selection
        mNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
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

        // Initialize Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Sync-ing is necessary to show the hamburger icon
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

