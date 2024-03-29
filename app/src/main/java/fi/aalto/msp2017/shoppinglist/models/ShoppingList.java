package fi.aalto.msp2017.shoppinglist.models;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Sunil on 11-04-2017.
 */

public class    ShoppingList {

    private String listName;
    private String owner;
    private HashMap<String, Object> timestampCreated;


    private String listID;
    public ShoppingList() {

    }
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


    public String getImageName() {
            return "alphabet_"+listName.substring(0,1).toLowerCase();
    }

    public boolean getSearchResult(String searchtext) {
        return listName.toLowerCase().contains(searchtext.toLowerCase());

    }
    @Exclude
    public String getListID() {
        return listID;
    }

    public void setListID(String listID) {
        this.listID = listID;
    }
}