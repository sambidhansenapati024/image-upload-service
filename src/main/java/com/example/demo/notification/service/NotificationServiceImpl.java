package com.example.demo.notification.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;
import com.example.demo.service.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl
        implements NotificationService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final EmailService emailService;

    public NotificationServiceImpl(
            EmailService emailService) {

        this.emailService = emailService;
    }

    @Override
    public void sendWelcomeNotification(User user) {

        Map<String, Object> variables =
                new HashMap<>();

        variables.put("name", user.getFullName());

        emailService.sendHtmlEmailAsync(

                user.getEmail(),

                "Welcome to CloudVault",

                "email/welcome",

                variables

        );
    }

    @Override
    public void sendPasswordChangedNotification(User user) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("name", user.getFullName());

        variables.put("time", LocalDateTime.now());

        emailService.sendHtmlEmail(

                user.getEmail(),

                "Your CloudVault Password Was Changed",

                "email/password-changed",

                variables

        );

    }

    @Override
    public void sendNewLoginNotification(
            User user,
            UserSession session) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("name", user.getFullName());

        variables.put("browser", session.getBrowser());

        variables.put("os", session.getOperatingSystem());

        variables.put("device", session.getDevice());

        variables.put("ip", session.getIpAddress());

        variables.put("time", session.getLoginTime());

        emailService.sendHtmlEmail(

                user.getEmail(),

                "New Sign-in to your CloudVault Account",

                "email/login-alert",

                variables

        );

    }

    @Override
    public void sendPasswordResetNotification(User user, String token) {

        Map<String, Object> variables = new HashMap<>();

        variables.put("name", user.getFullName());

        String resetLink =
                frontendUrl + "/reset-password?token=" + token;

        variables.put("resetLink", resetLink);

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Reset your CloudVault password",
                "email/password-reset",
                variables
        );
    }

}
