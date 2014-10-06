package nl.devapp.iboodnotifications;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import nl.devapp.iboodnotifications.listeners.OpenTitleContextualListener;


public class OpenTitleActivity extends ActionBarActivity {

    public ArrayList<String> wordsArray = new ArrayList<String>();
    public ArrayAdapter<String> wordsAdapter;

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wordsAdapter = new ArrayAdapter<String>(this, (android.os.Build.VERSION.SDK_INT >= 11) ? android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1, wordsArray);

        ListView wordsList = (ListView) findViewById(R.id.wordList);
        wordsList.setAdapter(wordsAdapter);

        loadList();

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // New android, API 11 and higer
            wordsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            wordsList.setMultiChoiceModeListener(new OpenTitleContextualListener(this));
        } else {
            //Old android, less then API 11
            registerForContextMenu(wordsList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.open_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.new_item:
                final EditText input = new EditText(this);
                input.setLines(1);
                input.setSingleLine();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getText(R.string.new_item));
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString().replace(";", "");

                        Log.d("OpenTitleActivity", "Adding: " + value);

                        if (value.length() != 0) {
                            wordsArray.add(value);
                            wordsAdapter.notifyDataSetChanged();

                            saveList();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                });
                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open_title_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {

            case R.id.item_delete:
                wordsArray.remove(info.position);
                wordsAdapter.notifyDataSetChanged();

                saveList();
                return true;

        }

        return super.onContextItemSelected(item);
    }

    public void saveList() {
        String wordsString = "";

        for (String s : wordsArray) {
            if (s.length() != 0) {
                wordsString += s + ";";
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Editor editor = preferences.edit();
        editor.putString("word_list", wordsString);
        editor.commit();
    }

    public void loadList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String wordsString = preferences.getString("word_list", "");

        for (String i : Arrays.asList(wordsString.split(";"))) {
            if (i.length() != 0) {
                wordsArray.add(i);
            }
        }

        wordsAdapter.notifyDataSetChanged();
    }
}
