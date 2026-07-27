package com.example.demo.notification.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserSession;

public interface NotificationService {

    void sendWelcomeNotification(User user);

    void sendPasswordChangedNotification(User user);

    void sendNewLoginNotification(
            User user,
            UserSession session
    );

    void sendPasswordResetNotification(User user, String token);
}
