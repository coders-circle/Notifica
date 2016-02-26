package com.toggle.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toggle.notifica.database.NetworkHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {

        final EditText usernameView = (EditText)findViewById(R.id.username);
        final EditText passwordView = (EditText)findViewById(R.id.password);
        final EditText firstNameView = (EditText)findViewById(R.id.first_name);
        final EditText lastNameView = (EditText)findViewById(R.id.last_name);
        final EditText emailView = (EditText)findViewById(R.id.email);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();
        final String firstName = firstNameView.getText().toString();
        final String lastName = lastNameView.getText().toString();
        final String email = emailView.getText().toString();

        if (username.equals("")) {
            usernameView.requestFocus();
            usernameView.setError("Username is required");
            return;
        }
        if (password.equals("")) {
            passwordView.requestFocus();
            passwordView.setError("Password is required");
            return;
        }

        // TODO: More offline validation

        if (!NetworkHandler.isNetworkAvailable(this)) {
            Toast.makeText(RegisterActivity.this, "You are not connected to internet. Check your connection and try again", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("first_name", firstName);
            json.put("last_name", lastName);
            json.put("email", email);
        } catch (JSONException e) {
            Toast.makeText(RegisterActivity.this, "Make sure all data are valid.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.register_progress);
        final View registerForm = findViewById(R.id.register_form);
        registerForm.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        NetworkHandler handler = new NetworkHandler(this, null, null, true);
        handler.post("classroom/api/v1/users/", json.toString(), new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (!result.success) {
                    progressBar.setVisibility(View.GONE);
                    registerForm.setVisibility(View.VISIBLE);
                }

                if (result.success) {
                    try {
                        JSONObject user = new JSONObject(result.result);

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
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

                        Toast.makeText(RegisterActivity.this, "Successfully registered. You are now logged in as " + username, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);

                        setResult(-1);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (result.code==400) {
                    try {
                        JSONObject error = new JSONObject(result.result);
                        if (error.has("username"))
                            usernameView.setError(error.getJSONArray("username").getString(0));
                        if (error.has("password"))
                            passwordView.setError(error.getJSONArray("password").getString(0));
                        if (error.has("email"))
                            emailView.setError(error.getJSONArray("email").getString(0));
                        if (error.has("first_name"))
                            firstNameView.setError(error.getJSONArray("first_name").getString(0));
                        if (error.has("last_name"))
                            lastNameView.setError(error.getJSONArray("last_name").getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Make sure all data are valid.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Couldn't register. Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
