package com.profecarlos.tallerapirest.restapi.service;

public class TransbankTransactionResponse {
    private String status;
    private String responseCode;
    private String message;
    private String transactionId;
    private String authorizationCode;
    private String token;
    private String url;

    public TransbankTransactionResponse() {
    }

    public TransbankTransactionResponse(String status, String responseCode, String message) {
        this.status = status;
        this.responseCode = responseCode;
        this.message = message;
    }

    // Getters y Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "TransbankTransactionResponse{" +
                "status='" + status + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", message='" + message + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", authorizationCode='" + authorizationCode + '\'' +
                ", token='" + token + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
