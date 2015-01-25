package co.kuntz.poverty.models;

import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Item {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    private long date;
    private String iname;
    private double amount;
    private List<String> categories;
    private String uname;

    public Item() {
        this.date = new Date().getTime();
    }

    public Item(String itemName, double amount, List<String> categories, String user) {
        this();
        this.iname = itemName;
        this.amount = amount;
        this.categories = categories;
        this.uname = user;
    }

    public long getDate() {
        return date;
    }

    public String getDateString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getDate());
        cal.setTimeZone(TimeZone.getDefault());


        return DATE_FORMAT.format(cal.getTime());
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getItemName() {
        return iname;
    }

    public void setItemName(String itemName) {
        this.iname = itemName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getCategoriesString() {
        return StringUtils.join(getCategories(), ", ");
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setCategories(String categories) {
        ArrayList<String> cats = new ArrayList<String>();
        for (String cat : Arrays.asList(categories.split(","))) {
            cats.add(cat.trim());
        }

        setCategories(cats);
    }

    public String getUser() {
        return uname;
    }

    public void setUser(String user) {
        this.uname = user;
    }

    public JSONObject toJson() {
        try {
            JSONObject json = new JSONObject();

            json.put("iname", getItemName());
            json.put("uname", getUser());
            json.put("date", getDate());
            json.put("amount", getAmount());
            json.put("categories", new JSONArray(getCategories()));

            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Item fromJson(JSONObject json) {
        try {
            Item item = new Item();

            item.setAmount(json.getDouble("amount"));
            item.setItemName(json.getString("iname"));
            item.setDate(json.getLong("date"));
            item.setUser(json.getString("uname"));

            JSONArray jsonCats = json.getJSONArray("categories");
            List<String> cats = new ArrayList<String>();
            for (int i = 0; i < jsonCats.length(); i++) {
                cats.add(jsonCats.getString(i));
            }
            item.setCategories(cats);

            return item;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestParams toRequestParams() {
        RequestParams params = new RequestParams();

        params.add("iname", getItemName());
        params.add("uname", getUser());
        params.add("date", String.valueOf(getDate()));
        params.add("amount", String.valueOf(getAmount()));
        params.add("categories", getCategoriesString());

        return params;
    }
}
