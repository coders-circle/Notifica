package com.toggle.notifica;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.toggle.notifica.database.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ClassSearchActivity extends AppCompatActivity implements ItemListAdapter.Listener {
    private String mUsername, mPassword;
    private List<ItemListAdapter.Item> mClasses = new ArrayList<>();
    private List<Long> mClassIds = new ArrayList<>();
    private ItemListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_search);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = preferences.getString("username", "");
        mPassword = preferences.getString("password", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view_classes);
        mAdapter = new ItemListAdapter(mClasses, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getClasses(null);
    }


    public void getClasses(String query) {
        // TODO: mutual exclusion
        mClasses.clear();
        mClassIds.clear();
        mAdapter.notifyDataSetChanged();

        String qUrl = "";
        if (query != null && !query.equals(""))
            try {
                qUrl = "?q=" + URLEncoder.encode(query, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        NetworkHandler handler = new NetworkHandler(this, mUsername, mPassword, true);
        handler.get("classroom/api/v1/classes/"+qUrl, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        JSONArray classes = new JSONArray(result.result);
                        for (int i = 0; i < classes.length(); ++i)
                            addClass(classes.getJSONObject(i));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(ClassSearchActivity.this, "Couldn't get class list.\n" +
                                "Make sure you are connected to internet and try again",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addClass(JSONObject json) throws JSONException {
        ItemListAdapter.Item item = new ItemListAdapter.Item();
        item.color = Utilities.returnColor(json.getLong("id"));
        item.title = json.getString("class_id");
        item.subTitle = json.getString("description");
        item.avatar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_avatar);
        getProfile(item, json.getLong("profile"));

        mClasses.add(item);
        mClassIds.add(json.getLong("id"));
        mAdapter.notifyDataSetChanged();
    }

    private void getProfile(final ItemListAdapter.Item item, long id) {
        final NetworkHandler handler = new NetworkHandler(this, mUsername, mPassword, true);
        handler.get("classroom/api/v1/profiles/" + id + "/", new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;

                        // Get the avatar of the profile
                        handler.getImage(json.getString("avatar"),
                                new NetworkHandler.NetworkListener() {
                                    @Override
                                    public void onComplete(NetworkHandler.Result result) {
                                        item.avatar = ((NetworkHandler.ImageResult) result).bitmap;
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_class_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Fetch the data remotely
                getClasses(query);
                // Reset SearchView
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                // Set activity title to search query
                ClassSearchActivity.this.setTitle(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public void onSelect(final int position, ItemListAdapter.Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Add the buttons
        builder.setPositiveButton(R.string.join, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendJoinRequest(mClassIds.get(position));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Join class: " + item.title + " ?");

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void sendJoinRequest(long id) {
        // Progress dialog

        // Check if request already exists

        // Post request

    }
}
