package fi.aalto.msp2017.shoppinglist.models;

/**
 * Created by sunil on 25-04-2017.
 */

public class Adverts {
    private Double latitude;
    private Double longitude;
    private String keyword;
    private String company;
    public Adverts(){}
    public Adverts(Double latitude, Double longitude, String keyword, String company) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.keyword = keyword;
        this.company = company;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
