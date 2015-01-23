package co.kuntz.poverty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import co.kuntz.poverty.http.HttpFuture;
import co.kuntz.poverty.http.PovertyHttpClient;
import co.kuntz.poverty.models.Item;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();
    public static final String PREFS_FILE = TAG + ".preferences";
    public static final String PREFS_USER_NAME = "userName";

    private String currentUser = "don"; // TODO make this dynamic using kickoff();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
            }

            @Override
            public void onFailure(String responseString, Throwable t) {
                Log.d(TAG, "Failed to get items!");
                Log.d(TAG, responseString, t);
            }
        });
    }
}
