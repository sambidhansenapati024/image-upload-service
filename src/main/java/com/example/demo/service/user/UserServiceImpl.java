package com.example.demo.service.user;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.SessionResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.repo.UserRepository;
import com.example.demo.repo.UserSessionRepository;
import com.example.demo.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private NotificationService notificationService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserSessionRepository userSessionRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserSessionRepository userSessionRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSessionRepository = userSessionRepository;
    }


    @Override
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException("Current password is incorrect");

        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new RuntimeException("Passwords do not match");

        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "New password must be different from the current password");

        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        try {

            notificationService.sendPasswordChangedNotification(user);

        } catch (Exception ex) {

            System.out.println("Error");

        }


    }

    @Override
    public List<SessionResponse> getActiveSessions() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentSessionId = getCurrentSessionId();

        List<UserSession> sessions =
                userSessionRepository.findByUserAndActiveTrueOrderByLoginTimeDesc(user);

        return sessions.stream()
                .map(session -> {

                    SessionResponse response = new SessionResponse();

                    response.setSessionId(session.getSessionId());

                    response.setCurrentSession(
                            session.getSessionId().toString().equals(currentSessionId)
                    );

                    response.setBrowser(session.getBrowser());
                    response.setOperatingSystem(session.getOperatingSystem());
                    response.setDevice(session.getDevice());
                    response.setLocation(session.getLocation());
                    response.setLoginTime(session.getLoginTime());
                    response.setLastActivity(session.getLastActivity());

                    return response;

                }).toList();

    }

    private String getCurrentSessionId() {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing Authorization header");
        }

        String token = authHeader.substring(7);

        return jwtService.extractSessionId(token);
    }

    @Override
    public void logoutSession(UUID sessionId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserSession session = userSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Security check
        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to logout this session.");
        }

        session.setActive(false);
        session.setLogoutTime(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    @Override
    public void logoutOtherSessions() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentSessionId = getCurrentSessionId();

        List<UserSession> sessions =
                userSessionRepository.findByUserAndActiveTrueOrderByLoginTimeDesc(user);

        for (UserSession session : sessions) {

            if (!session.getSessionId().toString().equals(currentSessionId)) {

                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());

            }

        }

        userSessionRepository.saveAll(sessions);

    }
}
