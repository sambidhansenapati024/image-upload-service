package com.example.demo.service.otp;

import com.example.demo.dto.*;
import com.example.demo.enums.OtpErrorCode;
import com.example.demo.enums.OtpPurpose;
import com.example.demo.model.OtpSession;
import com.example.demo.service.email.EmailService;
import com.example.demo.service.reids.RedisService;
import com.example.demo.util.OtpConstants;
import com.example.demo.util.OtpGenerator;
import com.example.demo.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public OtpResponse sendOtp(RegisterRequest request) {

        RegisterRequest redisRequest = new RegisterRequest();

        redisRequest.setFullName(
                request.getFullName()
        );

        redisRequest.setEmail(
                request.getEmail()
        );

        redisRequest.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        return saveOtpSession(redisRequest);

    }

    @Override
    public OtpVerificationResult verifyOtp(
            String email,
            String otp
    ) {

        String key = RedisKeys.otp(
                email,
                OtpPurpose.REGISTER
        );

        OtpSession session = redisService.get(
                key,
                OtpSession.class
        );

        if (session == null) {

            return new OtpVerificationResult(
                    false,
                    OtpErrorCode.OTP_EXPIRED,
                    null,
                    null
            );
        }

        if (!session.getOtp().equals(otp)) {

            session.setAttempts(session.getAttempts() + 1);

            if (session.getAttempts() >= OtpConstants.MAX_ATTEMPTS) {

                redisService.delete(key);

                return new OtpVerificationResult(
                        false,
                        OtpErrorCode.MAX_ATTEMPTS_EXCEEDED,
                        0,
                        null
                );
            }

            long ttl = redisService.getRemainingTtl(key);

            redisService.save(
                    key,
                    session,
                    ttl
            );

            int remainingAttempts =
                    OtpConstants.MAX_ATTEMPTS - session.getAttempts();

            return new OtpVerificationResult(
                    false,
                    OtpErrorCode.INVALID_OTP,
                    remainingAttempts,
                    null
            );
        }

        return new OtpVerificationResult(
                true,
                null,
                null,
                session.getRegisterRequest()
        );
    }


    @Override
    public OtpResponse resendOtp(
            ResendOtpRequest request) {

        String key = RedisKeys.otp(
                request.getEmail(),
                OtpPurpose.REGISTER
        );

        OtpSession session = redisService.get(
                key,
                OtpSession.class
        );

        if (session == null) {

            return new OtpResponse(
                    false,
                    "OTP session expired.",
                    OtpErrorCode.OTP_EXPIRED,
                    null
            );

        }

        // Password is already encoded in Redis
        return saveOtpSession(
                session.getRegisterRequest()
        );
    }
    private OtpResponse saveOtpSession(RegisterRequest request) {

        String key = null;

        try {

            String otp = OtpGenerator.generateOtp();

            OtpSession session = OtpSession.create(
                    request,
                    otp,
                    OtpPurpose.REGISTER
            );

            key = RedisKeys.otp(
                    request.getEmail(),
                    OtpPurpose.REGISTER
            );

            redisService.save(
                    key,
                    session,
                    OtpConstants.OTP_EXPIRY_SECONDS
            );

            Map<String, Object> variables = new HashMap<>();

            variables.put("title", "Verify your Email");

            variables.put(
                    "subtitle",
                    "Use the verification code below to complete your registration."
            );

            variables.put("otp", otp);

            variables.put("expiry", OtpConstants.OTP_EXPIRY_SECONDS);

            emailService.sendHtmlEmailAsync(

                    request.getEmail(),

                    "Verify your Email",

                    "email/otp-email",

                    variables

            );

            return new OtpResponse(
                    true,
                    "OTP generated successfully.",
                    null,
                    null
            );

        } catch (Exception e) {

            if (key != null) {
                redisService.delete(key);
            }

            return new OtpResponse(
                    false,
                    "Unable to send OTP.",
                    OtpErrorCode.EMAIL_SEND_FAILED,
                    null
            );
        }
    }
}