package com.example.demo.service.user;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.SessionResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    void changePassword(ChangePasswordRequest request);

    List<SessionResponse> getActiveSessions();

    void logoutSession(UUID sessionId);

    void logoutOtherSessions();

}
