package com.toggle.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toggle.notifica.database.Client;
import com.toggle.notifica.database.NetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If user is already logged in, go to the MainActivity
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains("username") && preferences.contains("password")
                && preferences.getBoolean("logged_in", false)) {
            startMainActivity();
            return;
        }

        // When login button is clicked, try logging in
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Make sure user can press log-in from keyboard when focused in password field
        ((EditText)findViewById(R.id.password)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login_ime || actionId == EditorInfo.IME_NULL) {
                    signIn();
                    return true;
                }
                return false;
            }
        });

        // When register button is clicked, start the register activity
        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn() {

        // On no internet connection, we do not even want to try to login
        if (!NetworkHandler.isNetworkAvailable(this)) {
            Toast.makeText(LoginActivity.this, "You are not connected to internet. Check your connection and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText usernameView = (EditText)findViewById(R.id.username);
        final EditText passwordView = (EditText)findViewById(R.id.password);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();

        // Validate the username and password to some extent in offline mode

        if (username.equals("")) {
            usernameView.requestFocus();
            usernameView.setError("Enter username");
            return;
        }
        if (password.equals("")) {
            passwordView.requestFocus();
            passwordView.setError("Enter password");
            return;
        }

        // Show login progress bar

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.login_progress);
        final View loginForm = findViewById(R.id.login_form);
        loginForm.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        // Next login from network
        login(username, password, new Listener() {
            @Override
            public void onResult(int code) {
                // On login failure, show back the login form and hide the progress bar
                if (code != 0) {
                    progressBar.setVisibility(View.GONE);
                    loginForm.setVisibility(View.VISIBLE);
                }

                switch (code) {
                    // On success, goto the main activity
                    case 0:
                        startMainActivity();
                        break;
                    // On failure, show respective error messages
                    case -1:
                        usernameView.requestFocus();
                        usernameView.setError("Invalid username");
                        break;
                    case -2:
                        passwordView.requestFocus();
                        passwordView.setError("Wrong password");
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Couldn't login. Check internet connection and try again", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private interface Listener {
        // returns 0 for success, -1 for invalid username, -2 for invalid password, -3 for any other failure
        void onResult(int code);
    }

    private void login(final String username, final String password, final Listener listener) {

        // First validate the username
        NetworkHandler unauthenticatedHandler = new NetworkHandler(this, null, null, true);
        unauthenticatedHandler.get("classroom/api/v1/users/?username=" + username, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {

                // The GET request was unsuccessful due to server or internet problems
                if (!result.success) {
                    listener.onResult(-3);
                    return;
                }

                try {
                    JSONArray users = new JSONArray(result.result);

                    // If user with such username isn't present, then error
                    if (users.length() != 1) {
                        listener.onResult(-1);
                        return;
                    }

                    // If user is present, we want to authenticate with password
                    final long user_id = users.getJSONObject(0).getLong("id");

                    // Now authenticate the username:password
                    NetworkHandler handler = new NetworkHandler(LoginActivity.this, username, password, true);
                    handler.get("classroom/api/v1/users/" + user_id + "/", new NetworkHandler.NetworkListener() {
                        @Override
                        public void onComplete(NetworkHandler.Result result) {

                            // On authentication failure, the GET request fails with 403 error code
                            if (!result.success) {
                                listener.onResult(result.code==403?-2:-3);
                                return;
                            }

                            try {
                                JSONObject user = new JSONObject(result.result);
                                if (!user.has("id") || user.getLong("id") != user_id)
                                    listener.onResult(-1);

                                // Successful login: save the username and password
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                preferences.edit()
                                        .putString("username", username)
                                        .putString("password", password)
                                        .putString("first_name", user.optString("first_name"))
                                        .putString("last_name", user.optString("last_name"))
                                        .putLong("profile", user.getLong("profile"))
                                        .putString("email", user.optString("email"))
                                        .putLong("id", user.getLong("id"))
                                        .putBoolean("logged_in", true)
                                        .apply();

                                // Get the user profile as well
                                Client client = new Client(LoginActivity.this, username, password);
                                client.addUser(user, new Client.ClientListener() {
                                    @Override
                                    public void refresh() {
                                        if (this.queue.size() == 0)
                                            listener.onResult(0);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onResult(-3);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onResult(-3);
                }
            }
        });
    }

    // Register activity finishes with some result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Register activity telling us to close
        if(requestCode == 1 && resultCode == -1)
            finish();
    }
}
