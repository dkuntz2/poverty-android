package co.kuntz.poverty;

import android.util.Log;

import junit.framework.TestCase;

import co.kuntz.poverty.models.Item;

public class ModelTest extends TestCase {
    private static final String TAG = ModelTest.class.getName();

    public void testItem() {
        Item i = new Item();
        i.setUser("don");
        i.setItemName("Something Cool");
        i.setCategories("things, cool");
        i.setAmount(45.65);

        Log.d(TAG, i.toJson());
    }
}
