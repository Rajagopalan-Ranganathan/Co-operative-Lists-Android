package fi.aalto.msp2017.shoppinglist.models;

/**
 * Created by sunil on 18-04-2017.
 */

public class User {
    private String name;
    private String email;
    private String imageUrl;
public User(){}
    public User(String name, String email, String imageUrl)
    {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void getByEmailID(String s) {

    }
}
