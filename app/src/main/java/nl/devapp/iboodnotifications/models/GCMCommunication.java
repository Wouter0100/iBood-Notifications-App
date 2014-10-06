package nl.devapp.iboodnotifications.models;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import nl.devapp.iboodnotifications.receivers.RegisterReceiver;
import nl.devapp.iboodnotifications.services.RegisterService;
import nl.devapp.iboodnotifications.tasks.SavePreferenceTask;

public class GCMCommunication {

    private static final String TAG = "GCMCommunication";

    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";
    private static final String SENDER_ID = "654806504568";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Context context;
    private SharedPreferences preferences;
    private GoogleCloudMessaging googleCloudMessaging;

    private String registrationId;

    public GCMCommunication(Context context) {
        this.context = context;

        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public boolean startRegistration() {
        Log.d(TAG, "startRegistration called, starting registration..");

        Intent registerIntent = new Intent(context, RegisterService.class);
        context.startService(registerIntent);

        return true;
    }

    public boolean finishRegistration(String registrationId) {
        Editor preferenceEditor = preferences.edit();
        preferenceEditor.putString(PROPERTY_REG_ID, registrationId);
        preferenceEditor.putInt(PROPERTY_APP_VERSION, getAppVersion());
        preferenceEditor.commit();

        this.registrationId = registrationId;

        // Sync properties with server.
        new SavePreferenceTask(context, null, null, null, null).execute(getRegistrationId(), "ibood_language", preferences.getString("ibood_language", "nl"));
        new SavePreferenceTask(context, null, null, null, null).execute(getRegistrationId(), "when_enabled", preferences.getString("when_enabled", "both"));
        new SavePreferenceTask(context, null, null, null, null).execute(getRegistrationId(), "realtime_hunt", preferences.getString("realtime_hunt", "wifi"));

        return true;
    }

    /*
     * getRegistrationId to receive the registration id.
     */
    public String getRegistrationId() {
        if (registrationId != null) {
            return registrationId;
        }

        registrationId = preferences.getString(PROPERTY_REG_ID, null);
        if (registrationId == null) {
            Log.i(TAG, "Registration not found");

            return null;
        }

        int registeredVersion = preferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();

        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed from " + registeredVersion + " to " + currentVersion);

            return null;
        }

        return registrationId;
    }

    /*
     * getAppVersion to receive current appVersion
     */
    private int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /*
     * checkPlayServices to check if Google Play Services is installed
     */
    public boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (activity != null) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
            }

            return false;
        }

        return true;
    }

    /*
     * needsRegistration to check if the device isn't registered and isn't registering.
     */
    public boolean needsRegistration() {
        Log.d(TAG, "needsRegisterion called");

        if (getRegistrationId() == null) {
            Log.d(TAG, "getRegistrationId is null");

            if (!isRegistering()) {
                Log.d(TAG, "isRegistering is false");

                return true;
            }
        }

        return false;
    }

    /*
     * isRegistering checks if the device is currently registering in background.
     */
    public boolean isRegistering() {
        Intent registerIntent = new Intent(context, RegisterReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, registerIntent, PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "isRegistering alarmUp is " + alarmUp);

        if (alarmUp) {
            return true;
        }

        registerIntent.putExtra("without_check", true);

        return (PendingIntent.getBroadcast(context, 0, registerIntent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public String register() {
        try {
            return googleCloudMessaging.register(SENDER_ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
