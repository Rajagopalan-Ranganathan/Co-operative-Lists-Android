package fi.aalto.msp2017.shoppinglist.models;
import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by Sunil on 11-04-2017.
 */

public class ShoppingList {

    private String listName;
    private String owner;
    private HashMap<String, Object> timestampCreated;


    public ShoppingList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNowObject;
    }
    public String getListName() {
        return listName;
    }

    public String getOwner() {
        return owner;
    }


    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }



    public void setTimestampLastChangedToNow() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
    }
}
