package com.example.demo.model;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.enums.OtpPurpose;

public class OtpSession {

    private RegisterRequest registerRequest;

    private String otp;

    private int attempts;

    private OtpPurpose purpose;

    public static OtpSession create(
            RegisterRequest registerRequest,
            String otp,
            OtpPurpose purpose) {

        OtpSession session = new OtpSession();

        session.setRegisterRequest(registerRequest);
        session.setOtp(otp);
        session.setAttempts(0);
        session.setPurpose(purpose);

        return session;
    }

    public RegisterRequest getRegisterRequest() {
        return registerRequest;
    }

    public void setRegisterRequest(RegisterRequest registerRequest) {
        this.registerRequest = registerRequest;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }
}