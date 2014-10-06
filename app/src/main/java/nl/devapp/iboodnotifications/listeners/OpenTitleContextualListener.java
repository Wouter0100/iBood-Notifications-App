package nl.devapp.iboodnotifications.listeners;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

import nl.devapp.iboodnotifications.OpenTitleActivity;
import nl.devapp.iboodnotifications.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OpenTitleContextualListener implements MultiChoiceModeListener {

    private static final String TAG = "OpenTitleContextualListener";

    private OpenTitleActivity openTitleActivity;

    public OpenTitleContextualListener(OpenTitleActivity openTitleActivity) {
        this.openTitleActivity = openTitleActivity;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.open_title_context, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_delete:
                ListView wordsList = (ListView) openTitleActivity.findViewById(R.id.wordList);
                SparseBooleanArray checkedItems = wordsList.getCheckedItemPositions();

                Log.i(TAG, "toString: " + checkedItems.toString());

                if (checkedItems != null) {
                    for (int i = checkedItems.size(); i > 0; i--) {
                        openTitleActivity.wordsArray.remove(checkedItems.keyAt(i - 1));
                        openTitleActivity.wordsAdapter.notifyDataSetChanged();
                        openTitleActivity.saveList();
                    }
                }

                mode.finish();
                return true;

        }

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }
}
