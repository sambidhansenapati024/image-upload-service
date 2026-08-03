package com.example.demo.service.pushNtification;

import com.example.demo.dto.ActivityLogResponseDto;

public interface NotificationWebSocketService {

    void sendToUser(String email, ActivityLogResponseDto notification);

}