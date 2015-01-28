package co.kuntz.poverty.activities;

import android.app.DatePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import co.kuntz.poverty.R;
import co.kuntz.poverty.http.HttpFuture;
import co.kuntz.poverty.http.PovertyHttpClient;
import co.kuntz.poverty.models.Item;


public class NewItemActivity extends ActionBarActivity {

    private static final String TAG = NewItemActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView dateView = (TextView) findViewById(R.id.item_date);
        final Calendar c = Calendar.getInstance();
        dateView.setText(Item.DATE_FORMAT.format(c.getTime()));

        Button changeDate = (Button) findViewById(R.id.button_change_item_date);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(NewItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);

                        dateView.setText(Item.DATE_FORMAT.format(c.getTime()));
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });

        Button newItemButton = (Button) findViewById(R.id.submit_item);
        newItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.item_name);
                EditText amount = (EditText) findViewById(R.id.item_amount);
                TextView date = (TextView) findViewById(R.id.item_date);
                EditText categories = (EditText) findViewById(R.id.item_categories);

                Log.d(TAG, "name = `" + name.getText().toString() + "`");
                Log.d(TAG, "amount = `" + amount.getText().toString() + "`");
                Log.d(TAG, "date = `" + date.getText().toString() + "`");
                Log.d(TAG, "categories = `" + categories.getText().toString() + "`");

                Log.d(TAG, "date in Unix Epoch = " + Item.DATE_FORMAT.parse(date.getText().toString(), new ParsePosition(0)).getTime());

                Item i = new Item();
                i.setUser(MainActivity.currentUser);
                i.setItemName(name.getText().toString());
                i.setAmount(Double.parseDouble(amount.getText().toString()));
                i.setDate(Item.DATE_FORMAT.parse(date.getText().toString(), new ParsePosition(0)).getTime());
                i.setCategories(categories.getText().toString());

                PovertyHttpClient.addItem(i, new HttpFuture<String>() {
                    @Override
                    public void onSuccess(String thing) {
                        Log.d(TAG, "httpFuture.success");
                        goHomeAndRefreshItems();
                    }

                    @Override
                    public void onFailure(String responseString, Throwable t) {
                        Log.d(TAG, "httpFuture.failure");
                        goHomeAndRefreshItems();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
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

    private void goHomeAndRefreshItems() {
        Log.d(TAG, "calling finish");
        finish();
    }
}
