package com.notifica.notifica;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";
    private Toolbar toolbar;
    Button test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test  = (Button) findViewById(R.id.test);
        test.setText("Test");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

    }
    public void Press(View v) {
        Intent i = new Intent(this,SubjectDetailsActivity.class);
        startActivity(i);
        Log.d(TAG, "Press: zzz");
    }

}
