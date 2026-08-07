package com.example.demo.util;

public final class OtpConstants {

    private OtpConstants() {
    }

    /**
     * OTP validity period (seconds)
     */
    public static final long OTP_EXPIRY_SECONDS = 60;

    /**
     * Maximum wrong attempts before OTP becomes invalid
     */
    public static final int MAX_ATTEMPTS = 3;

    /**
     * OTP Length
     */
    public static final int OTP_LENGTH = 4;

    /**
     * Redis key prefix
     */
    public static final String OTP_KEY_PREFIX = "otp:";

    /**
     * Verification session validity (5 minutes)
     */
    public static final long VERIFIED_SESSION_EXPIRY_SECONDS = 300;
}