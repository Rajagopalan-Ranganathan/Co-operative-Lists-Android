package fi.aalto.msp2017.shoppinglist.models;

/**
 * Created by sunil on 14-04-2017.
 */

public interface IItem {
    public String getItemName();
    public String getImageName();
    public String getItemKey();
    public String GetMoreDetails();
    public String getStatus();
}