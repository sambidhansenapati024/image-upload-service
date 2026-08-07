package com.example.demo.dto;

import com.example.demo.enums.OtpErrorCode;

public class OtpVerificationResult {

    private boolean success;

    private OtpErrorCode errorCode;

    private Integer remainingAttempts;

    private RegisterRequest registerRequest;

    public OtpVerificationResult() {
    }

    public OtpVerificationResult(
            boolean success,
            OtpErrorCode errorCode,
            Integer remainingAttempts,
            RegisterRequest registerRequest) {

        this.success = success;
        this.errorCode = errorCode;
        this.remainingAttempts = remainingAttempts;
        this.registerRequest = registerRequest;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public RegisterRequest getRegisterRequest() {
        return registerRequest;
    }

    public void setRegisterRequest(RegisterRequest registerRequest) {
        this.registerRequest = registerRequest;
    }
}