package co.kuntz.poverty.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import co.kuntz.poverty.R;
import co.kuntz.poverty.models.Item;

public class ItemView extends LinearLayout {
    private static final String TAG = ItemView.class.getName();

    View view;
    Item item;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.view_item, this);

        if (attrs == null) {
            return;
        }
    }

    public void set(Item item) {
        this.item = item;
        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        TextView itemAmount = (TextView) view.findViewById(R.id.item_amount);
        TextView itemDate = (TextView) view.findViewById(R.id.item_date);

        itemName.setText(item.getItemName());
        itemAmount.setText(String.format("$%1$,.2f", item.getAmount()));
        itemDate.setText(item.getDateString());
    }
}
