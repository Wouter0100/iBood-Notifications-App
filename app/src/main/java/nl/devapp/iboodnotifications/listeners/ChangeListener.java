package nl.devapp.iboodnotifications.listeners;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;

import nl.devapp.iboodnotifications.MainActivity;
import nl.devapp.iboodnotifications.R;
import nl.devapp.iboodnotifications.models.GCMCommunication;
import nl.devapp.iboodnotifications.preferences.BrowserPreference;
import nl.devapp.iboodnotifications.tasks.SavePreferenceTask;

public class ChangeListener implements OnPreferenceChangeListener {

    private static final String TAG = "ChangeListener";

    //Only load the first time, after the first time also save to API server.
    private boolean onlyLoad = true;
    private Context context;
    private MainActivity mainActivity;
    private GCMCommunication gcmCommunication;

    public ChangeListener(Context context, MainActivity mainActivity, GCMCommunication gcmCommunication) {
        this.context = context;
        this.mainActivity = mainActivity;
        this.gcmCommunication = gcmCommunication;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        Log.d(TAG, "onPreferenceChangeListener called (" + preference.toString() + " (" + preference.getKey() + ") and " + stringValue + ")!");

        if (!onlyLoad && !gcmCommunication.isRegistering() && (preference.getKey().equals("ibood_language") || preference.getKey().equals("when_enabled") || preference.getKey().equals("realtime_hunt"))) {
            new SavePreferenceTask(context, mainActivity, this, (ListPreference) preference, value).execute(gcmCommunication.getRegistrationId(), preference.getKey(), stringValue);

            return false;
        } else {
            onlyLoad = false;
        }

        if (preference.getKey().equals("enable_sound_on_deal")) {
            mainActivity.findPreference("sound_on_deal").setEnabled(((stringValue.equals("off")) ? false : true));
        }

        if (preference.getKey().equals("open_title_contains")) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String wordsString = preferences.getString("word_list", "");

            preference.setSummary(wordsString);

        } else if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(
                    (index >= 0)
                            ? listPreference.getEntries()[index]
                            : null);

        } else if (preference instanceof RingtonePreference) {

            if (TextUtils.isEmpty(stringValue)) {
                preference.setSummary(R.string.pref_ringtone_silent);

            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    preference.setSummary(null);
                } else {
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }

        } else if (preference instanceof BrowserPreference) {

            PackageManager pm = preference.getContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(stringValue, 0);
            } catch (final NameNotFoundException e) {
                ai = null;
            }

            preference.setSummary((ai != null ? pm.getApplicationLabel(ai) : null));

        } else {

            preference.setSummary(stringValue);

        }

        return true;
    }

    public void resetOnlyLoad() {
        onlyLoad = true;
    }

}
