package co.kuntz.poverty.views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import co.kuntz.poverty.R;
import co.kuntz.poverty.http.HttpFuture;
import co.kuntz.poverty.http.PovertyHttpClient;
import co.kuntz.poverty.models.Item;

public class ItemListAdapter extends BaseAdapter {
    private static final String TAG = ItemListAdapter.class.getName();

    private Activity activity;
    private List<Item> items;

    private HttpFuture<String> deleteFuture;

    public ItemListAdapter(Activity activity, List<Item> items, HttpFuture<String> deleteFuture) {
        this.activity = activity;
        this.items = items;
        this.deleteFuture = deleteFuture;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_item, null);
        }

        ItemView itemView = (ItemView) convertView.findViewById(R.id.item);
        itemView.set(items.get(position));

        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.button_delete);
        deleteButton.setVisibility(View.VISIBLE);
        Log.d(TAG, "deleteButton.isShown? " + (deleteButton.isShown()));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked delete button for item " + items.get(position).getItemName());

                PovertyHttpClient.removeItem(items.get(position).getId(), deleteFuture);
            }
        });

        return convertView;
    }
}
