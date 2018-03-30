package app.swift;

/**
 * Created by Admiral on 21/01/2018.
 */

public class Client_postRequests {

    private String client_id;
    private String courier_id;
    private String post_id;
    private String request_id;
    private String request_status;
    private Long requested_at;

    public Client_postRequests(String client_id, String courier_id, String post_id, String request_id, String request_status, Long requested_at) {
        this.client_id = client_id;
        this.courier_id = courier_id;
        this.post_id = post_id;
        this.request_id = request_id;
        this.request_status = request_status;
        this.requested_at = requested_at;
    }

    public Long getRequested_at() {
        return requested_at;
    }

    public void setRequested_at(Long requested_at) {
        this.requested_at = requested_at;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public Client_postRequests(){

    }
}
