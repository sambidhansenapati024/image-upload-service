package com.example.demo.dto;

import com.example.demo.enums.OtpErrorCode;

public class OtpResponse {


    private boolean success;

    private String message;

    private OtpErrorCode errorCode;

    private Integer remainingAttempts;

    public OtpResponse() {
    }

    public OtpResponse(boolean success, String message, OtpErrorCode errorCode, Integer remainingAttempts) {
        this.success = success;
        this.message = message;
        this.errorCode = errorCode;
        this.remainingAttempts = remainingAttempts;
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

    public OtpErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(OtpErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getRemainingAttempts() {
        return remainingAttempts;
    }

    public void setRemainingAttempts(Integer remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
    }
}
