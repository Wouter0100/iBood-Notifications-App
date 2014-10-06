package nl.devapp.iboodnotifications.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.devapp.iboodnotifications.adapters.BrowserAdapter;
import nl.devapp.iboodnotifications.models.Browser;

public class BrowserPreference extends DialogPreference {

    private static final String TAG = "BrowserPreference";

    private List<Browser> browserList;

    private String mValue;
    private boolean mValueSet;


    public BrowserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse("http://devapp.nl/"));

        List<ResolveInfo> resolveList = getContext().getPackageManager().queryIntentActivities(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        browserList = new ArrayList<Browser>();

        Log.i(TAG, resolveList.toString());

        for (ResolveInfo resolveItem : resolveList) {
            Browser browser = new Browser();
            browser.setBrowserInfo(getContext(), resolveItem);

            browserList.add(browser);

            Log.i(TAG, "App '" + browser.getPackageName() + "' with name '" + browser.getApplicationName() + "'");
        }

        ListAdapter browserAdapter = new BrowserAdapter(getContext(),
                browserList);

        builder.setAdapter(browserAdapter, this);

        super.onPrepareDialogBuilder(builder);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (callChangeListener(browserList.get(which).getPackageName())) {
            setValue(browserList.get(which).getPackageName());
        }

        Log.i(TAG, "Received on click, location " + which + " and name " + browserList.get(which).getApplicationName());
    }

    @Override
    public Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    public void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    private void setValue(String value) {
        final boolean changed = !TextUtils.equals(mValue, value);
        if (changed || !mValueSet) {
            mValue = value;
            mValueSet = true;
            persistString(value);
            if (changed) {
                notifyChanged();
            }
        }
    }
}
