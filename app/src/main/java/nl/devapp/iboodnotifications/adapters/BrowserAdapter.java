package nl.devapp.iboodnotifications.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nl.devapp.iboodnotifications.R;
import nl.devapp.iboodnotifications.models.Browser;

public class BrowserAdapter extends BaseAdapter {

    private static final String TAG = "BrowserAdapter";

    private final List<Browser> browserList;
    private final LayoutInflater inflater;

    public BrowserAdapter(Context context, List<Browser> browserList) {
        this.browserList = browserList;

        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (this.browserList == null || this.browserList.isEmpty()) {
            return 0;
        } else {
            return this.browserList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return this.browserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Browser browser = this.browserList.get(position);

        view = this.inflater.inflate(R.layout.list_pref_browser_row, null);

        ImageView icon = (ImageView) view.findViewById(R.id.app_icon);
        TextView name = (TextView) view.findViewById(R.id.app_title);

        icon.setImageDrawable(browser.getApplicationIcon());
        name.setText(browser.getApplicationName());

        return view;
    }

}
