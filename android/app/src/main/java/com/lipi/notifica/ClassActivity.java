package com.lipi.notifica;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lipi.notifica.database.DbHelper;
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

        // Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Class: " + mClass.class_id);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Open the news feed fragment inside this activity
        // Pass the class profile to the news feed fragment so
        // that only the posts for this class is displayed
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        if (mProfile != null)
            args.putLong("profile_id", mProfile._id);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();

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
}
