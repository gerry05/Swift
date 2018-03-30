package app.swift;

/**
 * Created by Admiral on 31/01/2018.
 */

public class Feedbacks {

    Long created_at;
    String feedback;
    String rate_by_user_od;
    String rating_feedback_id;
    String respondent_user_id;

    public Feedbacks(Long created_at, String feedback, String rate_by_user_od, String rating_feedback_id, String respondent_user_id) {
        this.created_at = created_at;
        this.feedback = feedback;
        this.rate_by_user_od = rate_by_user_od;
        this.rating_feedback_id = rating_feedback_id;
        this.respondent_user_id = respondent_user_id;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getRate_by_user_od() {
        return rate_by_user_od;
    }

    public void setRate_by_user_od(String rate_by_user_od) {
        this.rate_by_user_od = rate_by_user_od;
    }

    public String getRating_feedback_id() {
        return rating_feedback_id;
    }

    public void setRating_feedback_id(String rating_feedback_id) {
        this.rating_feedback_id = rating_feedback_id;
    }

    public String getRespondent_user_id() {
        return respondent_user_id;
    }

    public void setRespondent_user_id(String respondent_user_id) {
        this.respondent_user_id = respondent_user_id;
    }

    public Feedbacks(){

    }
}
