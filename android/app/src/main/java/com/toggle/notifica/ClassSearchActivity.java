package com.toggle.notifica;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.toggle.notifica.database.NetworkHandler;
import com.toggle.notifica.database.User;

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
    private TextView mTextView;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_search);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = preferences.getString("username", "");
        mPassword = preferences.getString("password", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Search class...");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_classes);
        mAdapter = new ItemListAdapter(mClasses, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getClasses(null);

        mTextView = (TextView)findViewById(R.id.new_class_text_view);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.base_url) + "classroom/add-class/";
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                startActivity(viewIntent);
            }
        });
        mRecyclerView.setVisibility(View.GONE);
    }

    private int mSearching = 0;
    public void getClasses(String query) {
        String qUrl = "";
        if (query == null || query.equals(""))
            return;

        mTextView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mTextView.setText("Searching...");

        mSearching++;
        final int mySearch = mSearching;

        mClasses.clear();
        mClassIds.clear();
        mAdapter.notifyDataSetChanged();

        try {
            qUrl = "?q=" + URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        NetworkHandler handler = new NetworkHandler(this, mUsername, mPassword, true);
        handler.get("classroom/api/v1/classes/"+qUrl, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (mSearching <= mySearch) {
                    boolean error = true;
                    if (result.success) {
                        try {
                            JSONArray classes = new JSONArray(result.result);
                            for (int i = 0; i < classes.length(); ++i)
                                addClass(classes.getJSONObject(i));
                            error = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (error)
                        Utilities.showMessage(ClassSearchActivity.this,
                                "Server connection failed. No internet?");
                }

                if (mClasses.size() > 0) {
                    mTextView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mTextView.setText("No results found\nClick here to create a new class");
                }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                Utilities.logout(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void sendJoinRequest(final long classId) {
        final User user = User.getLoggedInUser(this);
        if (user == null)
            return;

        // Progress dialog
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Selecting new elective");
        dialog.show();

        final NetworkHandler handler = new NetworkHandler(this, mUsername, mPassword, true);

        // Check if request already exists
        String queryString = "sender=" + user._id
                + "&sender_type=" + 0 + "&status=" + 0
                + "&request_type=" + 0 + "&to=" + classId;
        handler.get("api/v1/requests/?" + queryString, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (!result.success) {
                    dialog.dismiss();
                    Utilities.showMessage(ClassSearchActivity.this,
                            "Connection error while sending request.");
                    return;
                }

                try {
                    if (new JSONArray(result.result).length() > 0) {
                        dialog.dismiss();
                        Utilities.showMessage(ClassSearchActivity.this,
                                "You have already sent request to this class.");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Post request
                JSONObject request = new JSONObject();
                try {
                    request.put("sender", user._id);
                    request.put("sender_type", 0);
                    request.put("status", 0);
                    request.put("request_type", 0);
                    request.put("to", classId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.post("api/v1/requests/", request.toString(), new NetworkHandler.NetworkListener() {
                    @Override
                    public void onComplete(NetworkHandler.Result result) {
                        if (!result.success) {
                            dialog.dismiss();
                            Utilities.showMessage(ClassSearchActivity.this,
                                    "Connection error while sending request.");
                            return;
                        }

                        dialog.dismiss();
                        Utilities.showMessage(ClassSearchActivity.this,
                                "Join request sent");
                    }
                });
            }
        });

    }
}
