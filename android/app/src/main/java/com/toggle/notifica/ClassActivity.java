package com.toggle.notifica;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.Elective;
import com.toggle.notifica.database.PClass;
import com.toggle.notifica.database.Period;
import com.toggle.notifica.database.Profile;
import com.toggle.notifica.database.Routine;
import com.toggle.notifica.database.Subject;
import com.toggle.notifica.database.Teacher;
import com.toggle.notifica.database.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassActivity extends AppCompatActivity {

    private List<ItemListAdapter.Item> mSubjects = new ArrayList<>();
    private List<ItemListAdapter.Item> mTeachers = new ArrayList<>();
    private HashMap<String, List<ItemListAdapter.Item>> mElectives = new HashMap<>();
    private List<String> mElectiveGroups = new ArrayList<>();
    private PClass mClass;
    private DbHelper mDbHelper;

    public final static String[] PAGE_TITLES = {
        "Subjects", "Teachers", "Electives"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Get class profile

        mDbHelper = new DbHelper(this);
        String class_id = getIntent().getExtras().getString("class_id");
        mClass = PClass.get(PClass.class, mDbHelper, "class_id=?", new String[]{class_id}, null);
        Profile profile = Profile.get(Profile.class, mDbHelper, mClass.profile);

        // Set the toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(mClass.class_id);

        // Set the profile view

        View profileView = findViewById(R.id.profile);
        Utilities.fillProfileView(profileView, Utilities.returnColor(profile._id),
                profile.getAvatar(), null, mClass.class_id,
                mClass.description, null, null);

        profileView.findViewById(R.id.title).setVisibility(View.INVISIBLE);

        // Create the data for fragments

        refreshData();

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

    private void refreshData() {
        mSubjects.clear();
        mTeachers.clear();
        mElectives.clear();
        mElectiveGroups.clear();

        List<Long> sids = new ArrayList<>();
        List<Long> tids = new ArrayList<>();

        List<Routine> routines = mClass.getRoutines(mDbHelper);
        for (Routine r: routines) {
            List<Period> periods = r.getPeriods(mDbHelper);
            for (Period p: periods) {

                if (!sids.contains(p.subject)) {
                    sids.add(p.subject);
                    Subject s = p.getSubject(mDbHelper);

                    ItemListAdapter.Item subjectItem = new ItemListAdapter.Item();
                    subjectItem.title = s.name;
                    subjectItem.shortName = s.getShortName();
                    subjectItem.color = Color.parseColor(s.color);

                    Elective elective = s.getElective(mDbHelper);
                    if (elective == null) {
                        mSubjects.add(subjectItem);
                    }
                    else if (elective.p_class == mClass._id) {
                        if (mElectives.get(elective.p_group) == null) {
                            mElectives.put(elective.p_group, new ArrayList<ItemListAdapter.Item>());
                            mElectiveGroups.add(elective.p_group);
                        }

                        subjectItem.selected = elective.selected;
                        mElectives.get(elective.p_group).add(subjectItem);
                    }

                }

                List<Teacher> teachers = p.getTeachers(mDbHelper);
                for (Teacher t: teachers)
                    if (!tids.contains(t._id)) {
                        tids.add(t._id);

                        Bitmap avatar = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_default_avatar);

                        User user = t.getUser(mDbHelper);
                        if (user != null) {
                            Profile profile = user.getProfile(mDbHelper);
                            if (profile != null)
                                avatar = profile.getAvatar();
                        }

                        ItemListAdapter.Item teacherItem = new ItemListAdapter.Item();
                        teacherItem.title = t.getUsername(mDbHelper);
                        teacherItem.avatar = avatar;
                        teacherItem.color = Utilities.returnColor(t._id);
                        mTeachers.add(teacherItem);
                    }
            }
        }
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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PAGE_TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ItemListFragment() {
                        @Override
                        public List<ItemListAdapter.Item> getItems() {
                            return mSubjects;
                        }
                    };

                case 1:
                    return new ItemListFragment() {
                        @Override
                        public List<ItemListAdapter.Item> getItems() {
                            return mTeachers;
                        }
                    };
                case 2:
                    return new GroupItemListFragment() {
                        @Override
                        public List<String> getGroups() {
                            return mElectiveGroups;
                        }

                        @Override
                        public HashMap<String, List<ItemListAdapter.Item>> getItems() {
                            return mElectives;
                        }

                        @Override
                        public void selectItem(String group, ItemListAdapter.Item item,
                                               final GroupItemListAdapter.Listener callback) {
                            Subject subject = Subject.get(Subject.class, mDbHelper,
                                    "name=?", new String[]{item.title}, null);

                            if (subject == null)
                                return;

                            Elective elective = Elective.get(Elective.class, mDbHelper,
                                    "subject=? AND p_group=?", new String[]{subject._id+"",
                                    group}, null);

                            if (elective == null)
                                return;

                            final ProgressDialog dialog = new ProgressDialog(ClassActivity.this);
                            dialog.setMessage("Selecting new elective");
                            dialog.show();
                            elective.select(ClassActivity.this, new Elective.SelectCallback() {
                                @Override
                                public void onSelectionComplete() {
                                    refreshData();
                                    callback.refresh();
                                    dialog.dismiss();
                                }
                            });
                        }
                    };
                default:
                    return null;
            }
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            //do nothing here! no call to super.restoreState(arg0, arg1);
        }
    }
}
