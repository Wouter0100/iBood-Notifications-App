package nl.devapp.iboodnotifications.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.devapp.iboodnotifications.R;

public class Product {

    private static final String TAG = "Product";

    public static final int NOTIFICATION_ID = 4121;

    public static final int IMAGE = 0;
    public static final int SMALL_IMAGE = 1;

    private String fullName;
    private String shortName;

    private String image;
    private Bitmap imageBitmap;
    private String smallImage;
    private Bitmap smallImageBitmap;

    private String forumUrl;
    private String infoUrl;
    private String orderUrl;

    private String price;
    private String shipping;
    private String priceWithShipping;

    private boolean hunt;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getForumUrl() {
        return forumUrl;
    }

    public void setForumUrl(String forumUrl) {
        this.forumUrl = forumUrl;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(String infoUrl) {
        this.infoUrl = infoUrl;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(String orderUrl) {
        this.orderUrl = orderUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public Boolean getHunt() {
        return hunt;
    }
    
    public void setHunt(boolean hunt) {
        this.hunt = hunt;
    }

    public String getPriceWithShipping() {
        return priceWithShipping;
    }

    public void setPriceWithShipping(String priceWithShipping) {
        this.priceWithShipping = priceWithShipping;
    }

    public Bitmap getSmallImageBitmap() {
        return smallImageBitmap;
    }

    public void setSmallImageBitmap(Bitmap smallImageBitmap) {
        this.smallImageBitmap = smallImageBitmap;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void receiveImage(final Context context, final FutureCallback<Product> callback) {
        final Product product = this;

        Ion.with(context)
                .load(image)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        Resources res = context.getResources();
                        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
                        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
                        result = Bitmap.createScaledBitmap(result, width, height, false);

                        product.setImageBitmap(result);

                        callback.onCompleted(e, product);
                    }
                });
    }

    public void receiveSmallImage(final Context context, final FutureCallback<Product> callback) {
        final Product product = this;

        Ion.with(context)
                .load(smallImage)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        Resources res = context.getResources();
                        int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
                        int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
                        result = Bitmap.createScaledBitmap(result, width, height, false);

                        product.setSmallImageBitmap(result);

                        callback.onCompleted(e, product);
                    }
                });
    }

    public int createProductNotification(Context context, int progress, int priority, Bitmap largeIcon) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setSmallIcon(R.drawable.ic_launcher);
        notification.setLargeIcon(largeIcon);
        notification.setContentTitle(shortName);
        notification.setContentText(fullName);
        notification.setPriority(priority);
        notification.setOngoing(hunt); //If in hunt, onGoing is true!

        if (progress != -1) {
            notification.setProgress(100, progress, false);
        }

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.setData(Uri.parse(infoUrl));

        PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(pending);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification.build());

        return NOTIFICATION_ID;
    }
}
