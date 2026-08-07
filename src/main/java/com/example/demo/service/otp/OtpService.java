package com.example.demo.service.otp;

import com.example.demo.dto.*;

public interface OtpService {

    OtpResponse sendOtp(RegisterRequest request);

    OtpVerificationResult verifyOtp(
            String email,
            String otp
    );

    OtpResponse resendOtp(ResendOtpRequest request);

}