package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.AuthService;
import com.example.demo.service.password.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${app.cookie.secure}")
    private boolean secureCookie;

    @Autowired
    private PasswordResetService passwordResetService;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody CompleteRegistrationRequest request) {

        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        LoginResponse loginResponse =
                authService.login(
                        request,
                        httpRequest
                );

        String refreshToken =
                loginResponse.getRefreshToken();

        if (refreshToken != null) {

            ResponseCookie refreshCookie =
                    ResponseCookie.from(
                                    "refreshToken",
                                    refreshToken
                            )
                            .httpOnly(true)
                            .secure(secureCookie) // localhost
                            .sameSite("Lax")
                            .path("/auth")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    refreshCookie.toString()
            );
        }

        // Don't send refresh token in JSON
        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                        "If an account with that email exists, a password reset link has been sent."
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequests request) {

        passwordResetService.resetPassword(request);

        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @CookieValue(
                    value = "refreshToken",
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null ||
                refreshToken.isBlank()) {

            return ResponseEntity.status(
                    HttpStatus.UNAUTHORIZED
            ).body(
                    new RefreshTokenResponse(
                            false,
                            "Refresh token missing",
                            null,
                            null
                    )
            );
        }

        RefreshTokenResponse refreshResponse =
                authService.refreshToken(
                        refreshToken
                );

        if (refreshResponse.isSuccess() &&
                refreshResponse.getRefreshToken() != null) {

            String newRefreshToken =
                    refreshResponse.getRefreshToken();

            ResponseCookie refreshCookie =
                    ResponseCookie.from(
                                    "refreshToken",
                                    newRefreshToken
                            )
                            .httpOnly(true)
                            .secure(secureCookie)
                            .sameSite("Lax")
                            .path("/auth")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    refreshCookie.toString()
            );

            // Don't expose refresh token to Angular
            refreshResponse.setRefreshToken(null);
        }

        return ResponseEntity.ok(refreshResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(
                    value = "refreshToken",
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {

        // Revoke refresh token in DB + Redis
        authService.logout(refreshToken);

        // Delete refresh token cookie
        ResponseCookie deleteCookie =
                ResponseCookie.from(
                                "refreshToken",
                                ""
                        )
                        .httpOnly(true)
                        .secure(secureCookie)       // localhost
                        .sameSite("Lax")
                        .path("/auth")
                        .maxAge(0)
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                deleteCookie.toString()
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "success", true,
                        "message", "Logout successful"
                )
        );
    }
}