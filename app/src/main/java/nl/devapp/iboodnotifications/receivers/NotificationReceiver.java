package nl.devapp.iboodnotifications.receivers;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;

import nl.devapp.iboodnotifications.models.Product;
import nl.devapp.iboodnotifications.services.HuntService;

public class NotificationReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i("iBoodNotificationReceiver", "Received type: " + intent.getStringExtra("type"));

        if (intent.getStringExtra("type").equals("new_product")) {

            Product product = new Product();

            product.setFullName(intent.getStringExtra("full_name"));
            product.setShortName(intent.getStringExtra("short_name"));

            product.setImage(intent.getStringExtra("image"));
            product.setSmallImage(intent.getStringExtra("small_image"));

            product.setForumUrl(intent.getStringExtra("forum_url"));
            product.setInfoUrl(intent.getStringExtra("info_url"));
            product.setOrderUrl(intent.getStringExtra("order_url"));

            product.setPrice(intent.getStringExtra("price"));
            product.setShipping(intent.getStringExtra("shipping"));
            product.setPriceWithShipping(intent.getStringExtra("price_with_shipping"));

            product.setHunt(intent.getStringExtra("hunt").equals("true"));

            product.receiveSmallImage(context, new FutureCallback<Product>() {
                @Override
                public void onCompleted(Exception e, Product product) {
                    product.createProductNotification(context, -1, Notification.PRIORITY_DEFAULT, product.getSmallImageBitmap());
                }
            });

            setResultCode(Activity.RESULT_OK);

        } else if (intent.getStringExtra("type").equals("start_hunt")) {

            Intent huntIntent = new Intent(context, HuntService.class);

            context.startService(huntIntent);

        } else if (intent.getStringExtra("type").equals("stop_hunt")) {

            Intent huntIntent = new Intent(context, HuntService.class);

            context.stopService(huntIntent);
        }
    }
}