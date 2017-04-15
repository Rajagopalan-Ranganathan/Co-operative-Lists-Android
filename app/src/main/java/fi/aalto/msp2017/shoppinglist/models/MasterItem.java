package fi.aalto.msp2017.shoppinglist.models;

import android.text.TextUtils;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by sunil on 14-04-2017.
 */

public class MasterItem implements IItem {
    private String itemName;
    private Integer quantity;
    private HashMap<String, Object> timestampCreated;
    public MasterItem(){}

    private String imageName;

    public MasterItem(String itemName) {
        this.itemName = itemName;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNowObject;
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
}
