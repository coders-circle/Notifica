package com.lipi.notifica;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.PClass;
import com.lipi.notifica.database.Profile;

public class ClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // Get class profile

        DbHelper dbHelper = new DbHelper(this);
        String class_id = getIntent().getExtras().getString("class_id");
        PClass pClass = PClass.get(PClass.class, dbHelper, "class_id=?", new String[]{class_id}, null);
        Profile profile = Profile.get(Profile.class, dbHelper, pClass.profile);

        // Set the toolbar

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(pClass.class_id);

        // Set the profile view

        View profileView = findViewById(R.id.profile);
        Utilities.fillProfileView(profileView, Utilities.returnColor(profile._id),
                profile.getAvatar(), null, pClass.class_id,
                pClass.description, null, null);

        profileView.findViewById(R.id.title).setVisibility(View.INVISIBLE);

        // Set the recycler view

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.classRecyclerView);
        ClassAdapter adapter = new ClassAdapter(this, pClass);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
