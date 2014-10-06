package nl.devapp.iboodnotifications.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.devapp.iboodnotifications.R;
import nl.devapp.iboodnotifications.listeners.ChangeListener;

public class SavePreferenceTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "BrowserAdapter";

    private ChangeListener changeListener;
    private Activity activity;
    private ProgressDialog dialog;
    private Context context;
    private ListPreference preference;
    private Object value;

    public SavePreferenceTask(Context context, Activity activity, ChangeListener changeListener, ListPreference preference, Object value) {
        this.context = context;
        this.activity = activity;
        this.changeListener = changeListener;

        this.preference = preference;
        this.value = value;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (activity != null) {
            dialog = ProgressDialog.show(activity, "", context.getString(R.string.loading_wait), true);
        }
    }

    @Override
    protected Boolean doInBackground(String[] strings) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(context.getString(R.string.api_url) + "save/preference").openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");

            OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
            request.write("registration_id=" + strings[0] + "&preference=" + strings[1] + "&value=" + strings[2]);
            request.flush();
            request.close();

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            if (!reader.readLine().equals("OK")) {
                return false;
            }

            isr.close();
            reader.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);

        if (dialog != null) {
            dialog.cancel();
        }

        if (!success) {
            //Request to server failed, could because of no internet.

            if (activity != null) {
                Toast.makeText(context, context.getString(R.string.net_working_internet), Toast.LENGTH_LONG).show();
            }
        } else {
            if (changeListener != null) {
                //Request success, again, call oNPreferenceChange to save.
                changeListener.resetOnlyLoad();
                Log.i(TAG, "resetOnlyLoad because request success.");

                if (changeListener.onPreferenceChange(preference, value)) {
                    Log.i(TAG, "onPreferenceChange returned true.");

                    preference.setValue(value.toString());

                    Log.i(TAG, "Set new changes, key: '" + preference.getKey() + "' and value '" + value.toString() + "'");
                }
            }
        }
    }


}
