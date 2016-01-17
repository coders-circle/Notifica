package com.lipi.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference logout = findPreference("logout");
        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Clear log-in preferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                preferences.edit()
                        .putString("username", "")
                        .putString("password", "")
                        .putBoolean("logged_in", false).apply();

                // Start login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                // Finish the activities
                getActivity().setResult(-1);
                getActivity().finish();
                return true;
            }
        });
    }
}
