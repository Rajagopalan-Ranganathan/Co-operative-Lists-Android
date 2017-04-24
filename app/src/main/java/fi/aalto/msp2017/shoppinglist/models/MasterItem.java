package fi.aalto.msp2017.shoppinglist.models;

import android.text.TextUtils;

import com.firebase.client.ServerValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by sunil on 14-04-2017.
 */

public class MasterItem implements IItem {
    private String itemName;
    private String itemKey;
    private HashMap<String, Object> timestampCreated;
    DatabaseReference masterItemRef ;
    DatabaseReference listItemRef;
    DatabaseReference selfRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public MasterItem(){}

    private String imageName;

    public MasterItem(String itemName) {
        this.itemName = itemName;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNowObject;
    }
    public MasterItem(String itemName, String itemKey) {
        this(itemName);
        this.itemKey = itemKey;
    }

    @Exclude
    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
    public String getItemName() {
        return itemName;
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
    public String GetMoreDetails()
    {
        return "";
    }
    public String getStatus()
    {
        return "";
    }

    public void SaveToDB()
    {
        masterItemRef = database.getReference("masteritems");
        selfRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        String key = selfRef.child("items").push().getKey();
        this.setItemKey(key);
        selfRef = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        selfRef.child("items").child(key).setValue(this);
    }
}