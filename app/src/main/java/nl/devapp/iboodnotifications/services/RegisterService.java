package nl.devapp.iboodnotifications.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.devapp.iboodnotifications.R;
import nl.devapp.iboodnotifications.models.GCMCommunication;
import nl.devapp.iboodnotifications.receivers.RegisterReceiver;

public class RegisterService extends IntentService {

    private static final String TAG = "RegisterService";

    private GCMCommunication gcmCommunication;

    private String registrationId;

    private Context context;

    public RegisterService() {
        super("RegisterService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Log.i(TAG, "onCreate called.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent called, locking CPU..");

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RegisterService");
        wakeLock.acquire();

        gcmCommunication = new GCMCommunication(this);

        if (register()) {
            gcmCommunication.finishRegistration(registrationId);
        } else {
            Intent registerIntent = new Intent(this, RegisterReceiver.class);
            registerIntent.putExtra("without_check", true);

            PendingIntent registerPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 120 * 1000, registerPendingIntent);
        }

        wakeLock.release();
        RegisterReceiver.completeWakefulIntent(intent);

        Log.d(TAG, "onHandleIntent ended, CPU unlocked..");
    }

    private boolean register() {
        try {
            String registrationId = gcmCommunication.register();

            Log.i(TAG, "Registered and received registration id: " + registrationId);

            if (registrationId == null) {
                return false;
            }

            HttpURLConnection connection = (HttpURLConnection) new URL(context.getString(R.string.api_url) + "save/registration").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
            request.write("registration_id=" + registrationId);
            request.flush();
            request.close();

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            String response = reader.readLine();

            if (!response.equals("OK")) {
                Log.i(TAG, "Received invalid response from server: " + response);

                return false;
            }

            isr.close();
            reader.close();

            this.registrationId = registrationId;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
