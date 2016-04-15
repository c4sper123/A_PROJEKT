package android.testnavigation;

import java.util.Date;

public class Offer {
    private String locality;
    private String details;
    private String objectId;
    private Date startDate;
    private Integer price;
    private Integer type;
    private Date endDate;
    private String name;
    private String imageUrl;
    private Integer maxPeople;
    private String ownerId;

    public Offer(String name, String locality, String details,  Integer price,Integer type, String startDate,
                 String endDate, Integer maxPeople, String imageUrl, String objectId) {
        this.locality = locality;
        this.details = details;
        this.price = price;
        setStartDate(startDate);
        this.type = type;
        setEndDate(endDate);
        this.name = name;
        this.imageUrl = imageUrl;
        this.maxPeople = maxPeople;
        this.objectId = objectId;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }


    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getObjectId() {
        return objectId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        long stamp = Long.parseLong(startDate);
        Date date = new Date(stamp);
        this.startDate = date;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        long stamp = Long.parseLong(endDate);
        Date date = new Date(stamp);
        this.endDate = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }

    public String getOwnerId() {
        return ownerId;
    }

}