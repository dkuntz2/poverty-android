package co.kuntz.poverty.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import co.kuntz.poverty.R;
import co.kuntz.poverty.http.HttpFuture;
import co.kuntz.poverty.http.PovertyHttpClient;
import co.kuntz.poverty.models.Item;
import co.kuntz.poverty.views.ItemListAdapter;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    public static final String PREFS_FILE = TAG + ".preferences";
    public static final String PREFS_USER_NAME = "userName";

    public static String currentUser = "don"; // TODO make this dynamic using kickoff();

    private ItemListAdapter itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ListView listView = (ListView) findViewById(R.id.list_items);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.action_button);
        fab.attachToListView(listView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewItemActivity.class);
                startActivity(intent);
            }
        });

        //kickoff();
        initializeData(); // TODO remove this and switch to kickoff
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void kickoff() {
        if (currentUser != null) {
            initializeData();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String sharedPrefsUserName = sharedPreferences.getString(PREFS_USER_NAME, null);

        if (sharedPrefsUserName != null) {
            currentUser = sharedPrefsUserName;
            initializeData();
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Dialog Message");
        builder.setTitle("Dialog Title");

        final EditText userNameInput = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        userNameInput.setLayoutParams(lp);
        builder.setView(userNameInput);

        builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Clicked OK!");
                Log.d(TAG, "Username = " + userNameInput.getText());

                currentUser = userNameInput.getText().toString();

                initializeData();
            }
        });

        builder.show();
    }

    public void initializeData() {
        PovertyHttpClient.getItems(currentUser, new HttpFuture<List<Item>>() {
            @Override
            public void onSuccess(List<Item> things) {
                Log.d(TAG, "Got items!");
                Log.d(TAG, "things.size() = " + things.size());
                for (Item thing : things) {
                    Log.d(TAG, ".. " + thing.getItemName());
                }

                ListView listView = (ListView) findViewById(R.id.list_items);
                itemListAdapter = new ItemListAdapter(MainActivity.this, things, new HttpFuture<String>() {
                    @Override
                    public void onSuccess(String thing) {
                        initializeData();
                    }

                    @Override
                    public void onFailure(String responseString, Throwable t) {
                        Log.d(TAG, "error removing item...", t);
                    }
                });
                listView.setAdapter(itemListAdapter);
            }

            @Override
            public void onFailure(String responseString, Throwable t) {
                Log.d(TAG, "Failed to get items!");
                Log.d(TAG, responseString, t);
            }
        });
    }
}
