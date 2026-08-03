package com.example.demo.service.pushNtification;

import com.example.demo.dto.ActivityLogResponseDto;
import com.example.demo.service.ImageUploadServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.user.SimpUserRegistry;

@Service
public class NotificationWebSocketServiceImpl
        implements NotificationWebSocketService {

    @Autowired
    private SimpUserRegistry simpUserRegistry;
    private static final Logger logger =
            LoggerFactory.getLogger(NotificationWebSocketServiceImpl.class);

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketServiceImpl(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendToUser(String email,
                           ActivityLogResponseDto notification) {

        logger.info("Sending notification to user: {}", email);
        logger.info("Notification id={}", notification.getId());
        logger.info("Notification message={}", notification.getMessage());
        logger.info("Notification user={}", email);

        logger.info("Connected users: {}", simpUserRegistry.getUsers());

        messagingTemplate.convertAndSendToUser(
                email,
                "/queue/notifications",
                notification
        );
    }
}