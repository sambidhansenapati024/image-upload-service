package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionResponse {

    private UUID sessionId;
    private String browser;
    private String operatingSystem;
    private String device;
    private String location;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private Boolean currentSession;

    public SessionResponse() {
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Boolean getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Boolean currentSession) {
        this.currentSession = currentSession;
    }
}
