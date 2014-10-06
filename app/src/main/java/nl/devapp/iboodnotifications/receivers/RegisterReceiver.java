package nl.devapp.iboodnotifications.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nl.devapp.iboodnotifications.models.GCMCommunication;

public class RegisterReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "RegisterReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        GCMCommunication gcmCommunication = new GCMCommunication(context);

        if (gcmCommunication.needsRegistration()) {
            Log.d(TAG, "Started Registration..");
            gcmCommunication.startRegistration();
        }
    }

}
