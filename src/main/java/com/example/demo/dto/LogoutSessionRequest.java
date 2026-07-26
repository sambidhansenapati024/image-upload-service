package com.example.demo.dto;


import java.util.UUID;

public class LogoutSessionRequest {

    private UUID sessionId;

    public LogoutSessionRequest() {
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }
}
