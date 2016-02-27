package com.toggle.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.toggle.notifica.database.Client;
import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.PClass;
import com.toggle.notifica.database.PGroup;
import com.toggle.notifica.database.Period;
import com.toggle.notifica.database.Profile;
import com.toggle.notifica.database.Student;
import com.toggle.notifica.database.Subject;
import com.toggle.notifica.database.User;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;
    private DbHelper mDbHelper;
    private PClass mClass = null;

    private AppBarLayout mAppBarLayout;

    private RoutineFragment mRoutineFragment;
    private NewsFeedFragment mNewsFeedFragment;

    private ActionBarDrawerToggle mDrawerToggle;

    boolean isVisible = true;
    boolean swap = true;

    public PClass getPClass() { return mClass; }

    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener;

    public void initializeApp() {
        // Initialize database

        // Clean up unnecessary cache data
        mDbHelper.clean();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Client.ClientListener refreshCallback = new Client.ClientListener() {
                    @Override
                    public void refresh() {
                        // if (queue.size() == 0) {
                        if (mMenu != null)
                            refreshMenuItems();
                        // }
                    }
                };

                // Download and get new routine
                final Client client = new Client(MainActivity.this);
                client.getRoutine(refreshCallback);

                // Get the latest profile for logged in user
                User user = User.getLoggedInUser(MainActivity.this);
                client.getUser(user._id, refreshCallback);
                client.getProfile(user.profile, refreshCallback);

                // Also get all student and teacher profiles associated with the account
                client.getAssociated("student", user._id, refreshCallback);
                client.getAssociated("teacher", user._id, refreshCallback);
            }
        }).run();

        // Get GCM token if not available
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = preferences.getString("gcm_token", "");
        if (token.equals("")) {
            Intent intent = new Intent(this, GcmRegisterService.class);
            startService(intent);
        }

        // Send GCM token to server if not already sent
        token = preferences.getString("gcm_token", "");
        if (!token.equals(""))
            if (!preferences.getBoolean("gcm_token_sent", false))
                GcmRegisterService.sendRegistrationToServer(this,  token);
    }

    // Set user profile data in header
    private void setHeaderView() {
        // Get views
        View headerView = mNavigationView.getHeaderView(0);
        TextView username = (TextView)headerView.findViewById(R.id.username);
        TextView info = (TextView)headerView.findViewById(R.id.info);
        ImageView avatar = (ImageView)headerView.findViewById(R.id.avatar);

        // Get user profile
        User user = User.getLoggedInUser(this);
        Profile profile = Profile.get(Profile.class, mDbHelper, user.profile);
        Student student = user.getStudent(mDbHelper);

        // Set header data
        username.setText(user.getName());

        // If student set "class (group)" as info text
        if (student != null) {
            PGroup myGroup = student.getGroup(mDbHelper);
            mClass = myGroup.getPClass(mDbHelper);

            String infoText = mClass.class_id + " (" + myGroup.group_id + ")";
            info.setText(infoText);
        }
        // else set email as info text
        else
            info.setText(user.email);

        if (profile != null) {
            avatar.setImageBitmap(profile.getAvatar());
            ((GradientDrawable)avatar.getBackground()).setColor(0xFFFFFFFF);
        } else {
            avatar.setBackgroundResource(R.drawable.ic_default_avatar);
        }

        // Also set routine header
        fillHeader();
    }

    // Add a menu item to a group with given id, name and icon
    private MenuItem addMenuItem(int group, int id, String name, int icon) {
        MenuItem menuItem = mMenu.add(group, id, Menu.NONE, name);
        menuItem.setIcon(icon);
        menuItem.setCheckable(true);
        menuItem.setChecked(false);
        return menuItem;
    }

    private void refreshMenuItems() {
        // Add basic items
        mMenu.clear();
        addMenuItem(R.id.basic_group, R.id.news_feed, "News Feed", R.mipmap.news_feed);
        addMenuItem(R.id.basic_group, R.id.routine, "Routine", R.mipmap.routine);
        addMenuItem(R.id.basic_group, R.id.assignment, "Assignments", R.mipmap.assignment);
        addMenuItem(R.id.settings_group, R.id.settings, "Settings", R.mipmap.settings);

        // Get classes
        List<PClass> classes = PClass.getAll(PClass.class, new DbHelper(this));

        // Add menu item for each class
        for (PClass pClass: classes) {
            addMenuItem(R.id.classes_group, 0, pClass.class_id, R.mipmap.ic_launcher);
        }

        // Set items visibility
        mMenu.setGroupVisible(R.id.basic_group, isVisible);
        mMenu.setGroupVisible(R.id.settings_group, isVisible);
        mMenu.setGroupVisible(R.id.classes_group, !isVisible);

        // Set the drawer header contents from the user profile
        try {
            setHeaderView();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapMenu() {
        final View headerView = mNavigationView.getHeaderView(0);
        final ImageView swapClasses = (ImageView) headerView.findViewById(R.id.class_select);

        isVisible = !isVisible;
        if (swap) {
            swapClasses.setImageResource(R.mipmap.close);
            swap = false;
        } else {
            swapClasses.setImageResource(R.mipmap.swap_class);
            swap = true;
        }
        mMenu.setGroupVisible(R.id.basic_group, isVisible);
        mMenu.setGroupVisible(R.id.settings_group, isVisible);
        mMenu.setGroupVisible(R.id.classes_group, !isVisible);
    }

    // Prepare the navigation drawer menu
    private void prepareMenu() {
        // Add checkable groups to the menu
        mMenu = mNavigationView.getMenu();
        mMenu.setGroupCheckable(R.id.basic_group, true, true);
        mMenu.setGroupCheckable(R.id.settings_group, true, true);

        // Add menu items
        refreshMenuItems();

        // Initialize the swap button in the drawer
        final View headerView = mNavigationView.getHeaderView(0);
        final ImageView swapClasses = (ImageView) headerView.findViewById(R.id.class_select);

        swapClasses.setImageResource(R.mipmap.swap_class);
        ((View)swapClasses.getParent()).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();

                if (mClass != null) {
                    Intent intent1 = new Intent(MainActivity.this, ClassActivity.class);
                    intent1.putExtra("class_id", mClass.class_id);
                    startActivity(intent1);
                }

                // swapMenu();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new DbHelper(this);
        initializeApp();

        // Create the fragments once
        mRoutineFragment = new RoutineFragment();
        mNewsFeedFragment = new NewsFeedFragment();

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        fillHeader();

        // Initialize the navigation drawer
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

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
                        mAppBarLayout.setExpanded(false, true);
                        break;
                    case R.id.assignment:
                        break;
                    case R.id.settings:
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, 1);
                        return false;
                    default:
                        swapMenu();
                        mDrawerLayout.closeDrawers();
                        String class_id = menuItem.getTitle().toString();
                        Intent intent1 = new Intent(MainActivity.this, ClassActivity.class);
                        intent1.putExtra("class_id", class_id);
                        startActivity(intent1);
                        return false;
                }

                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, selectedFragment).commit();
                    menuItem.setChecked(true);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        };
        mNavigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        // Initialize Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,R.string.openDrawer, R.string.closeDrawer);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

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

        mNavigationItemSelectedListener.onNavigationItemSelected(mNavigationView.getMenu().findItem(startPage));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Settings activity telling us to close
        if(requestCode == 1 && resultCode == -1)
            finish();
    }

    public void fillHeader() {
        DbHelper dbHelper = new DbHelper(this);
        Calendar cal = Calendar.getInstance();

        View view1 = findViewById(R.id.now);
        View view2 = findViewById(R.id.next);

        // Get current time and day of week
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Get current and next periods
        Period current = Period.get(Period.class, dbHelper, "start_time<=? AND end_time>? AND day=?", new String[]{"" + currentTime, "" + currentTime, "" + day}, "start_time");
        Period next = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{"" + currentTime, "" + day}, "start_time");

        // If next period isn't today, get tomorrow's period and so on
        int count = 0;
        while (next == null && count < 7) {
            day = (day + 1) % 7;
            next = Period.get(Period.class, dbHelper, "day=?", new String[]{"" + day}, "start_time");
            count++;
        }

        int remaining = 60;
        if (next != null) {
            // Find remaining time to next period
            remaining = next.start_time - currentTime;
            if (count > 0)
                remaining = 24 * 60 - currentTime + (count - 1) * 24 + next.start_time;

            Subject subject = Subject.get(Subject.class, dbHelper, next.subject);

            // Show current period if exists
            if (current != null) {
                Subject sub = Subject.get(Subject.class, dbHelper, current.subject);
                if (sub != null) {
                    Utilities.fillProfileView(
                            view1, Color.parseColor(sub.color), null, "Current class",
                            sub.name, current.getStartTime() + " - " + current.getEndTime(),
                            null, sub.getShortName()
                    );
                    view1.setVisibility(View.VISIBLE);
                    findViewById(R.id.now_practical).setVisibility(
                            current.period_type==1?View.VISIBLE:View.GONE);
                } else
                    view1.setVisibility(View.GONE);
            } else
                view1.setVisibility(View.GONE);

            // Show next period

            String text = "";
            if (count == 1)
                text += "Tomorrow ";
            else if (count > 1) {
                text += DbHelper.DAYS[day] + " ";
            }
            text += next.getStartTime() + " - " + next.getEndTime();

            //String text2 = "Next in " + Utilities.formatMinutes(remaining);
            Utilities.fillProfileView(
                    view2, Color.parseColor(subject.color), null, "Next class", subject.name,
                    text, null, subject.getShortName()
            );
            view2.setVisibility(View.VISIBLE);
            findViewById(R.id.next_practical).setVisibility(
                    next.period_type == 1 ? View.VISIBLE : View.GONE);
        } else {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }

        int nextMinute = remaining*60*1000; //(60-cal.get(Calendar.SECOND))*1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fillHeader();
            }
        }, nextMinute);
    }
}

