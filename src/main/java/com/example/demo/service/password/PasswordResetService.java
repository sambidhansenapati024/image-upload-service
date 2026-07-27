package com.example.demo.service.password;

import com.example.demo.dto.ResetPasswordRequests;
import com.example.demo.entity.User;

public interface PasswordResetService {

    String createPasswordResetToken(User user);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequests request);
}
