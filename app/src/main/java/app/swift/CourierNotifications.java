package app.swift;

/**
 * Created by Admiral on 30/01/2018.
 */

public class CourierNotifications {
    private String message;
    private String notification_id;
    private String notification_receiver_user_id;
    private String notification_sender_user_id;
    private Long created_at;
    private String rating_feedback;

    public CourierNotifications(String message, String notification_id, String notification_receiver_user_id, String notification_sender_user_id, Long created_at, String rating_feedback) {
        this.message = message;
        this.notification_id = notification_id;
        this.notification_receiver_user_id = notification_receiver_user_id;
        this.notification_sender_user_id = notification_sender_user_id;
        this.created_at = created_at;
        this.rating_feedback = rating_feedback;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(String notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_receiver_user_id() {
        return notification_receiver_user_id;
    }

    public void setNotification_receiver_user_id(String notification_receiver_user_id) {
        this.notification_receiver_user_id = notification_receiver_user_id;
    }

    public String getNotification_sender_user_id() {
        return notification_sender_user_id;
    }

    public void setNotification_sender_user_id(String notification_sender_user_id) {
        this.notification_sender_user_id = notification_sender_user_id;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public String getRating_feedback() {
        return rating_feedback;
    }

    public void setRating_feedback(String rating_feedback) {
        this.rating_feedback = rating_feedback;
    }

    public CourierNotifications(){

    }
}
