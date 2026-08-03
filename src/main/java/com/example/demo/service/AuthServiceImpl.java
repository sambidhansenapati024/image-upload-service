package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.enums.ActionType;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.repo.ProfileRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.UserSessionRepository;
import com.example.demo.service.pushNtification.ActivityLogService;
import com.example.demo.service.user.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private JwtService jwtService;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegisterResponse(false, "Email already exists");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

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

        return new RegisterResponse(true, "User Registered Successfully");
    }

    @Override
    public LoginResponse login(LoginRequest request,  HttpServletRequest httpRequest) {


        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null);
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return new LoginResponse(false,
                    "Invalid Email or Password",
                    null);
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

        String token = jwtService.generateToken(
                user.getEmail(),
                session.getSessionId().toString()
        );

        if (!knownDevice) {

            activityLogService.logNewLogin(user.getEmail(), session);

        }

        return new LoginResponse(
                true,
                "Login Successful",
                token
        );
    }
}
