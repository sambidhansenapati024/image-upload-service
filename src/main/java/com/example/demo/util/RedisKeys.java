package com.example.demo.util;

import com.example.demo.enums.OtpPurpose;

public final class RedisKeys {

    private RedisKeys() {
    }

    public static String otp(String email, OtpPurpose purpose) {

        return "otp:" + purpose.name() + ":" + email.toLowerCase();

    }


}