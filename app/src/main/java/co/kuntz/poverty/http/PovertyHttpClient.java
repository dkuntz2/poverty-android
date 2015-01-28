package co.kuntz.poverty.http;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.kuntz.poverty.models.Item;

public class PovertyHttpClient {
    private static final String TAG = PovertyHttpClient.class.getName();

    public static final String BASE_URL = "http://192.168.56.1:3000";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getItems(final String username, final HttpFuture<List<Item>> caller) {
        RequestParams params = new RequestParams();
        params.put("uname", username);

        client.get(BASE_URL + "/data/get", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);

                Log.d(TAG, "/data/get | uname: " + username);
                Log.d(TAG, response.toString());

                List<Item> items = new ArrayList<Item>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        items.add(Item.fromJson(response.getJSONObject(i)));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

                caller.onSuccess(items);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                caller.onFailure(responseString, throwable);
            }
        });
    }

    public static void removeItem(final int id, final HttpFuture<String> caller) {
        RequestParams params = new RequestParams();
        params.put("id", id);

        client.get(BASE_URL + "/data/remove", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                caller.onFailure(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (!responseString.toLowerCase().equals("\"success\"")) {
                    caller.onFailure(responseString, null);
                    return;
                }

                Log.d(TAG, "successfully deleted item `" + id + "`");

                caller.onSuccess(responseString);
            }
        });
    }

    public static void addItem(final Item item, final HttpFuture<String> caller) {
        RequestParams params = item.toRequestParams();

        client.get(BASE_URL + "/data/add", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "Successfully added item");
                caller.onSuccess(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "FAILED!");
                caller.onFailure(responseString, throwable);
            }
        });
    }
}
