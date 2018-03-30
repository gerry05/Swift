package app.swift;

/**
 * Created by Admiral on 14/01/2018.
 */

public class UsersPosts {


    private String pickup_location;
    private String dropoff_location;
    private String amount;
    private String distance;
    private String user_profileImage;
    private String client_id;
    private String post_id;
    private String post_status;
    private Long posted_at;

    public UsersPosts(String pickup_location, String dropoff_location, String amount, String distance, String user_profileImage, String client_id, String post_id, String post_status, Long posted_at) {
        this.pickup_location = pickup_location;
        this.dropoff_location = dropoff_location;
        this.amount = amount;
        this.distance = distance;
        this.user_profileImage = user_profileImage;
        this.client_id = client_id;
        this.post_id = post_id;
        this.post_status = post_status;
        this.posted_at = posted_at;
    }

    public Long getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(Long posted_at) {
        this.posted_at = posted_at;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getPost_status() {
        return post_status;
    }

    public void setPost_status(String post_status) {
        this.post_status = post_status;
    }



    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getDropoff_location() {
        return dropoff_location;
    }

    public void setDropoff_location(String dropoff_location) {
        this.dropoff_location = dropoff_location;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUser_profileImage() {
        return user_profileImage;
    }

    public void setUser_profileImage(String user_profileImage) {
        this.user_profileImage = user_profileImage;
    }



    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public UsersPosts(){

    }
}
