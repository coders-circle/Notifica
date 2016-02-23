package com.lipi.notifica;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Department;
import com.lipi.notifica.database.PClass;
import com.lipi.notifica.database.Profile;

public class ClassActivity extends AppCompatActivity {

    private PClass mClass;
    private Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        DbHelper dbHelper = new DbHelper(this);
        String class_id = getIntent().getExtras().getString("class_id");
        mClass = PClass.get(PClass.class, dbHelper, "class_id=?", new String[]{class_id}, null);
        mProfile = Profile.get(Profile.class, dbHelper, mClass.profile);
        Department department = mClass.getDepartment(dbHelper);

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Class");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Set the profile view
        Utilities.fillProfileView(findViewById(R.id.profile), mProfile._id,
                mProfile.getAvatar(), mClass.class_id,
                mClass.description, null);

        // Initialize the tabs
        ClassTabsPagerAdapter adapter = new ClassTabsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.class_pager);
        viewPager.setAdapter(adapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.class_tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return ContextCompat.getColor(ClassActivity.this, R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(viewPager);

        viewPager.setCurrentItem(0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public class ClassTabsPagerAdapter extends FragmentStatePagerAdapter {
        public ClassTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "News Feed";
        }

        @Override
        public Fragment getItem(int position) {
            if (position < 0 || position > 0)
                return null;

            // Create the news feed fragment
            // Pass the class profile to the news feed fragment so
            // that only the posts for this class is displayed
            NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
            Bundle args = new Bundle();
            if (mProfile != null)
                args.putLong("profile_id", mProfile._id);
            newsFeedFragment.setArguments(args);
            return newsFeedFragment;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            //do nothing here! no call to super.restoreState(arg0, arg1);
        }
    }
}
