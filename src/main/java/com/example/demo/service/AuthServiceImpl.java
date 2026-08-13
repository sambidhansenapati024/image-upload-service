package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.enums.ActionType;
import com.example.demo.enums.OtpPurpose;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.repo.ProfileRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.UserSessionRepository;
import com.example.demo.service.otp.OtpService;
import com.example.demo.service.pushNtification.ActivityLogService;
import com.example.demo.service.refreshToken.RefreshTokenRedisService;
import com.example.demo.service.reids.RedisService;
import com.example.demo.service.user.UserServiceImpl;
import com.example.demo.util.RedisKeys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRedisService refreshTokenRedisService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    protected RedisService redisService;

    @Autowired
    private OtpService otpService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse register(
            CompleteRegistrationRequest request
    ) {
        OtpVerificationResult result =
                otpService.verifyOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        if (!result.isSuccess()) {

            return new RegisterResponse(
                    false,
                    getOtpErrorMessage(result)
            );

        }

        RegisterRequest registerRequest =
                result.getRegisterRequest();

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return new RegisterResponse(false, "Email already exists");
        }

        User user = new User();

        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());

        // Encrypt password
        user.setPassword(registerRequest.getPassword());

        User savedUser = userRepository.save(user);
        activityLogService.logActivity(
                savedUser.getEmail(),
                ActionType.WELCOME,
                "🎉 Welcome to CloudVault! Start uploading your memories securely.",
                null
        );

        Profile profile = new Profile();
        profile.setUser(savedUser);

        profileRepository.save(profile);
        try {

            notificationService.sendWelcomeNotification(user);

        } catch (Exception ex) {

           System.out.println("Error occure");

        }

        redisService.delete(
                RedisKeys.otp(
                        registerRequest.getEmail(),
                        OtpPurpose.REGISTER
                )
        );

        return new RegisterResponse(true, "User Registered Successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request,  HttpServletRequest httpRequest) {


        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null,null);
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null,null);
        }

        DeviceInfo deviceInfo =
                deviceInfoService.extract(httpRequest);

        boolean knownDevice = userSessionRepository
                .existsByUserAndBrowserAndOperatingSystemAndDevice(
                        user,
                        deviceInfo.getBrowser(),
                        deviceInfo.getOperatingSystem(),
                        deviceInfo.getDevice()
                );

        UserSession session = new UserSession();

        session.setUser(user);

        session.setBrowser(deviceInfo.getBrowser());

        session.setOperatingSystem(deviceInfo.getOperatingSystem());

        session.setDevice(deviceInfo.getDevice());

        session.setIpAddress(deviceInfo.getIpAddress());

// We'll populate this later using GeoIP
        session.setLocation(null);

        userSessionRepository.save(session);

//        String token = jwtService.generateToken(
//                user.getEmail(),
//                session.getSessionId().toString()
//        );
//
//        if (!knownDevice) {
//
//            activityLogService.logNewLogin(user.getEmail(), session);
//
//        }
//
//        return new LoginResponse(
//                true,
//                "Login Successful",
//                token
//        );

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                session.getSessionId().toString()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getEmail(),
                session.getSessionId().toString()
        );

        String refreshTokenJti =
                jwtService.extractJti(refreshToken);

        session.setRefreshTokenHash(
                hashToken(refreshToken)
        );

        session.setRefreshTokenJti(
                refreshTokenJti
        );

        session.setRefreshTokenExpiresAt(
                LocalDateTime.now()
                        .plusNanos(
                                jwtService.getRefreshExpiration()
                                        * 1_000_000
                        )
        );

        session.setRefreshTokenRotationCount(0);

        userSessionRepository.save(session);

        refreshTokenRedisService.store(
                refreshTokenJti,
                session.getSessionId().toString(),
                Duration.ofMillis(
                        jwtService.getRefreshExpiration()
                )
        );

        return new LoginResponse(
                true,
                "Login Successful",
                accessToken,
                refreshToken
        );
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) {

        // 1. Validate refresh token
        if (!jwtService.isRefreshTokenValid(refreshToken)) {

            return new RefreshTokenResponse(
                    false,
                    "Invalid or expired refresh token",
                    null,
                    null
            );
        }

        // 2. Extract information
        String sessionId =
                jwtService.extractSessionId(refreshToken);

        String jti =
                jwtService.extractJti(refreshToken);

        String email =
                jwtService.extractEmail(refreshToken);

        // 3. Check Redis
        String redisSessionId =
                refreshTokenRedisService
                        .getSessionId(jti);

        if (redisSessionId == null) {

            return new RefreshTokenResponse(
                    false,
                    "Refresh token is no longer valid",
                    null,
                    null
            );
        }

        // 4. Make sure Redis session matches JWT
        if (!redisSessionId.equals(sessionId)) {

            return new RefreshTokenResponse(
                    false,
                    "Invalid refresh token",
                    null,
                    null
            );
        }

        // 5. Find session in DB
        Optional<UserSession> optionalSession =
                userSessionRepository.findBySessionId(
                        java.util.UUID.fromString(sessionId)
                );

        if (optionalSession.isEmpty()) {

            return new RefreshTokenResponse(
                    false,
                    "Session not found",
                    null,
                    null
            );
        }

        UserSession session =
                optionalSession.get();

        // 6. Check whether session is active
        if (!Boolean.TRUE.equals(session.getActive())) {

            return new RefreshTokenResponse(
                    false,
                    "Session is inactive",
                    null,
                    null
            );
        }

        // 7. Check rotation limit
        Integer rotationCount =
                session.getRefreshTokenRotationCount();

        if (rotationCount == null) {
            rotationCount = 0;
        }

        if (rotationCount >=
                jwtService.getMaxRefreshRotations()) {

            session.setActive(false);
            session.setLogoutTime(LocalDateTime.now());

            userSessionRepository.save(session);

            refreshTokenRedisService.delete(jti);

            return new RefreshTokenResponse(
                    false,
                    "Session expired. Please login again.",
                    null,
                    null
            );
        }

        // 8. Verify refresh token against DB hash
        String storedHash =
                session.getRefreshTokenHash();

        String receivedHash =
                hashToken(refreshToken);

        if (storedHash == null ||
                !storedHash.equals(receivedHash)) {

            return new RefreshTokenResponse(
                    false,
                    "Invalid refresh token",
                    null,
                    null
            );
        }

        // 9. Generate NEW access token
        String newAccessToken =
                jwtService.generateToken(
                        email,
                        sessionId
                );

        // 10. Generate NEW refresh token
        String newRefreshToken =
                jwtService.generateRefreshToken(
                        email,
                        sessionId
                );

        String newJti =
                jwtService.extractJti(
                        newRefreshToken
                );

        // 11. Remove old refresh token from Redis
        refreshTokenRedisService.delete(jti);

        // 12. Store new refresh token in Redis
        refreshTokenRedisService.store(
                newJti,
                sessionId,
                Duration.ofMillis(
                        jwtService.getRefreshExpiration()
                )
        );

        // 13. Update DB session
        session.setRefreshTokenHash(
                hashToken(newRefreshToken)
        );

        session.setRefreshTokenJti(
                newJti
        );

        session.setRefreshTokenExpiresAt(
                LocalDateTime.now()
                        .plusNanos(
                                jwtService
                                        .getRefreshExpiration()
                                        * 1_000_000
                        )
        );

        session.setRefreshTokenRotationCount(
                rotationCount + 1
        );

        session.setLastActivity(
                LocalDateTime.now()
        );

        userSessionRepository.save(session);

        // 14. Return new tokens
        return new RefreshTokenResponse(
                true,
                "Token refreshed successfully",
                newAccessToken,
                newRefreshToken
        );
    }

    @Override
    public void logout(String refreshToken) {

        if (refreshToken == null ||
                refreshToken.isBlank()) {

            return;
        }

        // Validate refresh token
        if (!jwtService.isRefreshTokenValid(refreshToken)) {

            return;
        }

        String sessionId =
                jwtService.extractSessionId(refreshToken);

        String jti =
                jwtService.extractJti(refreshToken);

        // Remove refresh token from Redis
        refreshTokenRedisService.delete(jti);

        // Find session
        Optional<UserSession> optionalSession =
                userSessionRepository.findBySessionId(
                        java.util.UUID.fromString(sessionId)
                );

        if (optionalSession.isEmpty()) {

            return;
        }

        UserSession session =
                optionalSession.get();

        // Mark session inactive
        session.setActive(false);

        session.setLogoutTime(
                LocalDateTime.now()
        );

        session.setUpdatedAt(
                LocalDateTime.now()
        );

        // Clear refresh token information
        session.setRefreshTokenHash(null);

        session.setRefreshTokenJti(null);

        session.setRefreshTokenExpiresAt(null);

        session.setRefreshTokenRotationCount(null);

        userSessionRepository.save(session);
    }

    private String getOtpErrorMessage(OtpVerificationResult result) {

        return switch (result.getErrorCode()) {
            case OTP_EXPIRED -> "OTP has expired.";
            case INVALID_OTP -> "Invalid OTP. Remaining attempts: "
                    + result.getRemainingAttempts();
            case MAX_ATTEMPTS_EXCEEDED -> "Maximum OTP attempts exceeded.";
            default -> "OTP verification failed.";
        };
    }


    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(0xff & b);

                if (hex.length() == 1) {

                    hexString.append('0');

                }

                hexString.append(hex);

            }

            return hexString.toString();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to hash refresh token",
                    e
            );

        }
    }

}
