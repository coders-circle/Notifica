package com.toggle.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.toggle.notifica.database.DbHelper;

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
                // Show warning before logging out
                Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                        "You are going to logout.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("LOGOUT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Utilities.logout(getActivity());

                                // refresh widgets
                                PeriodWidgetProvider.updateAllWidgets(getActivity());

                                // Finish the activities
                                getActivity().setResult(-1);
                                getActivity().finish();
                            }
                        }).show();
                return true;
            }
        });
    }
}
