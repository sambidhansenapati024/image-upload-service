package com.example.demo.service;

import com.example.demo.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.max-refresh-rotations}")
    private int maxRefreshRotations;


    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

    }


    // =========================================================
    // ACCESS TOKEN
    // =========================================================

    public String generateToken(
            String email,
            String sessionId,
            UserType userType
    ) {

        return Jwts.builder()

                .subject(email)

                .claim("sessionId", sessionId)

                .claim("type", "ACCESS")

                .claim("userType",userType.name())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(getKey())

                .compact();
    }


    // =========================================================
    // REFRESH TOKEN
    // =========================================================

    public String generateRefreshToken(
            String email,
            String sessionId,
            UserType userType
    ) {

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()

                .subject(email)

                .claim("sessionId", sessionId)

                .claim("type", "REFRESH")

                .claim("userType",userType.name())

                .id(jti)

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + refreshExpiration
                        )
                )

                .signWith(getKey())

                .compact();
    }


    // =========================================================
    // EXTRACT EMAIL
    // =========================================================

    public String extractEmail(String token) {

        Claims claims = parseToken(token);

        return claims.getSubject();
    }


    // =========================================================
    // EXTRACT SESSION ID
    // =========================================================

    public String extractSessionId(String token) {

        Claims claims = parseToken(token);

        return claims.get(
                "sessionId",
                String.class
        );
    }


    // =========================================================
    // EXTRACT TOKEN TYPE
    // =========================================================

    public String extractTokenType(String token) {

        Claims claims = parseToken(token);

        return claims.get(
                "type",
                String.class
        );
    }


    // =========================================================
    // EXTRACT JTI
    // =========================================================

    public String extractJti(String token) {

        Claims claims = parseToken(token);

        return claims.getId();
    }


    public UserType extractUserType(String token) {

        Claims claims = parseToken(token);

        return claims.get(
                "userType",
                UserType.class
        );
    }

    // =========================================================
    // ACCESS TOKEN VALIDATION
    // =========================================================

    public boolean isTokenValid(String token) {

        try {

            Claims claims = parseToken(token);

            return claims.getExpiration()
                    .after(new Date());

        } catch (Exception e) {

            return false;

        }

    }


    // =========================================================
    // REFRESH TOKEN VALIDATION
    // =========================================================

    public boolean isRefreshTokenValid(String token) {

        try {

            Claims claims = parseToken(token);

            String type = claims.get(
                    "type",
                    String.class
            );

            if (!"REFRESH".equals(type)) {

                return false;

            }

            return claims.getExpiration()
                    .after(new Date());

        } catch (Exception e) {

            return false;

        }

    }


    // =========================================================
    // PARSE TOKEN
    // =========================================================

    private Claims parseToken(String token) {

        return Jwts.parser()

                .verifyWith(getKey())

                .build()

                .parseSignedClaims(token)

                .getPayload();

    }


    // =========================================================
    // CONFIGURATION GETTERS
    // =========================================================

    public long getRefreshExpiration() {

        return refreshExpiration;

    }


    public int getMaxRefreshRotations() {

        return maxRefreshRotations;

    }

}
