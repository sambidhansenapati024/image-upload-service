package com.example.demo.controller;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LogoutSessionRequest;
import com.example.demo.dto.SessionResponse;
import com.example.demo.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/sessions")
    public List<SessionResponse> getActiveSessions() {
        return userService.getActiveSessions();
    }

    @PostMapping("/logout-session")
    public ResponseEntity<Void> logoutSession(
            @RequestBody LogoutSessionRequest request) {

        userService.logoutSession(request.getSessionId());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-other-sessions")
    public ResponseEntity<Void> logoutOtherSessions() {

        userService.logoutOtherSessions();

        return ResponseEntity.ok().build();

    }


}
