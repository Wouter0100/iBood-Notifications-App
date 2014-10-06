package nl.devapp.iboodnotifications.models;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class Browser {

    private static final String TAG = "Browser";

    private ResolveInfo browserInfo;
    private Context context;

    public void setBrowserInfo(Context context, ResolveInfo browserInfo) {
        this.browserInfo = browserInfo;
        this.context = context;
    }

    public ResolveInfo getBrowserInfo() {
        return this.browserInfo;
    }

    public String getPackageName() {
        return browserInfo.activityInfo.packageName;
    }

    public String getApplicationName() {
        return browserInfo.loadLabel(context.getPackageManager()).toString();
    }

    public Drawable getApplicationIcon() {
        return browserInfo.loadIcon(context.getPackageManager());
    }

}
