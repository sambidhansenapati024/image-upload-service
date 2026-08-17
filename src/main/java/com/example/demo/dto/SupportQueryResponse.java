package com.example.demo.dto;

public class SupportQueryResponse {

    private boolean success;

    private String message;

    private Long queryId;


    public SupportQueryResponse() {
    }


    public SupportQueryResponse(
            boolean success,
            String message,
            Long queryId) {

        this.success = success;
        this.message = message;
        this.queryId = queryId;

    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Long getQueryId() {
        return queryId;
    }

    public void setQueryId(Long queryId) {
        this.queryId = queryId;
    }

}
