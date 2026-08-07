package com.example.demo.util;

import java.security.SecureRandom;

public final class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpGenerator() {
    }

    public static String generateOtp() {

        int otp = RANDOM.nextInt(9000) + 1000;

        return String.valueOf(otp);
    }

}