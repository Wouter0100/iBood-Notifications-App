package nl.devapp.iboodnotifications.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodSession;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

public class HuntService extends Service {

    private static final String TAG = "HuntService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "onStartCommand");

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://192.168.1.2:3000", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, SocketIOClient client) {

                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }

                client.setStringCallback(new StringCallback() {
                    @Override
                    public void onString(String s, Acknowledge acknowledge) {
                        System.out.println(s);
                    }
                });

                client.on("someEvent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray argument, Acknowledge acknowledge) {
                        System.out.println("args: " + argument.toString());
                    }
                });

                client.setJSONCallback(new JSONCallback() {
                    @Override
                    public void onJSON(JSONObject jsonObject, Acknowledge acknowledge) {
                        System.out.println("json: " + jsonObject.toString());
                    }
                });

            }
        });

        return START_STICKY;
    }
}
