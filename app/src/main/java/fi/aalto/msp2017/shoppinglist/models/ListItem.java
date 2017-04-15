package fi.aalto.msp2017.shoppinglist.models;

import android.text.TextUtils;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by sunil on 12-04-2017.
 */

public class ListItem implements IItem {
    private String itemName;
    private String owner;

    private String itemId;
    private Integer quantity;
    private HashMap<String, Object> timestampCreated;
    public ListItem(){}

    private String imageName;

    public ListItem(String itemName, String owner) {
        this.itemName = itemName;
        this.owner = owner;
        this.quantity = 0;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNowObject;
    }
    public String getItemName() {
        return itemName;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public String getImageName()
    {
        if (TextUtils.isEmpty(imageName))
            return "alphabet_"+itemName.substring(0,1).toLowerCase();
        else
            return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean getSearchResult(String search) {
        return itemName.toLowerCase().contains(search.toLowerCase());
    }

    public void setTimestampLastChangedToNow() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
