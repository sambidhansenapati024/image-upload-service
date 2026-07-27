package com.example.demo.util;

import java.util.UUID;

public final class TokenGenerator {

    private TokenGenerator() {
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

}
