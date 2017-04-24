package fi.aalto.msp2017.shoppinglist.models;

import android.text.TextUtils;

import com.firebase.client.ServerValue;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by sunil on 12-04-2017.
 */

public class ListItem implements IItem {

    private String itemName;
    private String owner;
    private Integer quantity;
    private HashMap<String, Object> timestampCreated;
    private String itemKey;
    private String status = "(Not Purchased)";
    DatabaseReference masterItemRef;
    DatabaseReference listItemRef;
    DatabaseReference selfRef;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public ListItem(){}

    private String imageName;

    private ListItem(String itemName, String owner) {
        this.itemName = itemName;
        this.owner = owner;
        this.quantity = 1;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNowObject;
    }
    public ListItem(String listName, String owner, String itemKey) {
        this(listName, owner);
        this.itemKey = itemKey;
    }
    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
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


    public String GetMoreDetails()
    {
        if (!TextUtils.isEmpty(getOwner()))
            return "x " + getQuantity().toString()+" by "+ getOwner();
        return "x " + getQuantity().toString();
    }


    public void SaveToDB(String listID)
    {
        listItemRef = database.getReference("shoppinglist").child(listID).child("items").child(getItemKey());
        listItemRef.setValue(this);

    }


    public String getStatus() {
        if(TextUtils.isEmpty(status)) {
            return "(Not Purchased)";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}