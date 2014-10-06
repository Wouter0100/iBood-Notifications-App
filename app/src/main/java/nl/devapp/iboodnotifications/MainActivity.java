package nl.devapp.iboodnotifications;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import nl.devapp.iboodnotifications.listeners.ChangeListener;
import nl.devapp.iboodnotifications.models.GCMCommunication;

public class MainActivity extends PreferenceActivity {

    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleCloudMessaging googleCloudMessaging;
    private String registrationId;

    private Context context;

    private GCMCommunication gcmCommunication;

    /*
     * onCreate is called when the app launches
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        gcmCommunication = new GCMCommunication(context);

        if (gcmCommunication.checkPlayServices(this)) {
            if (gcmCommunication.needsRegistration()) {
                Log.d(TAG, "Registering device..");

                gcmCommunication.startRegistration();
            }
        } else {
            Toast.makeText(context, this.getString(R.string.device_not_supported), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /*
     * onPostCreate is called when the app launches
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupPreferencesScreen();
    }

    /*
     * onResume is called when a user returns to the app.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!gcmCommunication.checkPlayServices(this)) {
            Toast.makeText(context, this.getString(R.string.device_not_supported), Toast.LENGTH_LONG).show();
            finish();
        }

        bindPreferences();
    }

    /*
     * setupPreferencesScreen setups the preferences screen.
     */
    @SuppressWarnings("deprecation")
    private void setupPreferencesScreen() {
        // Add 'General' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'Auto Opener' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_auto_opener);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_auto_opener);

        // Add 'Notifications' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        bindPreferences();
    }

    /*
     * bindPreferences calls for all Preferences the bindPreferenceSummaryToValue if needed.
     */
    private void bindPreferences() {
        bindPreferenceSummaryToValue(findPreference("ibood_language"));
        bindPreferenceSummaryToValue(findPreference("when_enabled"));
        bindPreferenceSummaryToValue(findPreference("realtime_hunt"));
        bindPreferenceSummaryToValue(findPreference("browser_selection"));
        bindPreferenceSummaryToValue(findPreference("sound_on_open"));
        bindPreferenceSummaryToValue(findPreference("open_title_contains"));
        bindPreferenceSummaryToValue(findPreference("enable_vibrate_on_deal"));
        bindPreferenceSummaryToValue(findPreference("enable_sound_on_deal"));
        bindPreferenceSummaryToValue(findPreference("sound_on_deal"));
    }

    /*
     * bindPreferenceSummaryToValue bind's the Preferences Summarys (under the Preference) to the Values.
     */
    private void bindPreferenceSummaryToValue(Preference preference) {

        ChangeListener changeListener = new ChangeListener(context, this, gcmCommunication);

        preference.setOnPreferenceChangeListener(changeListener);

        changeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
