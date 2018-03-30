package app.swift;

/**
 * Created by Admiral on 31/01/2018.
 */

public class TransactionHistory {

    String client_id;
    Long completed_at;
    String courier_id;
    String post_id;
    String request_id;
    String transaction_id;
    String transaction_status;

    public TransactionHistory(String client_id, Long completed_at, String courier_id, String post_id, String request_id, String transaction_id, String transaction_status) {
        this.client_id = client_id;
        this.completed_at = completed_at;
        this.courier_id = courier_id;
        this.post_id = post_id;
        this.request_id = request_id;
        this.transaction_id = transaction_id;
        this.transaction_status = transaction_status;
    }

    public String getCourier_id() {
        return courier_id;
    }

    public void setCourier_id(String courier_id) {
        this.courier_id = courier_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Long getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Long completed_at) {
        this.completed_at = completed_at;
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

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_status() {
        return transaction_status;
    }

    public void setTransaction_status(String transaction_status) {
        this.transaction_status = transaction_status;
    }

    public TransactionHistory(){

    }
}
