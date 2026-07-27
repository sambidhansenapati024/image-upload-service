package com.example.demo.service.password;

import com.example.demo.dto.ResetPasswordRequests;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.notification.service.NotificationService;
import com.example.demo.repo.PasswordResetTokenRepository;
import com.example.demo.repo.UserRepository;
import com.example.demo.util.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private  PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

@Autowired
private PasswordEncoder passwordEncoder;

    @Override
    public String createPasswordResetToken(User user) {

        PasswordResetToken passwordResetToken = new PasswordResetToken();

        passwordResetToken.setUser(user);
        String token = TokenGenerator.generateToken();

        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));

        List<PasswordResetToken> activeTokens =
                passwordResetTokenRepository.findByUserAndUsedFalse(user);

        for (PasswordResetToken tokens : activeTokens) {
            tokens.setUsed(true);
        }

        passwordResetTokenRepository.save(passwordResetToken);

        return token;
    }

    @Override
    public void forgotPassword(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        String token = createPasswordResetToken(user);

        try {

            notificationService.sendPasswordResetNotification(
                    user,
                    token
            );

        } catch (Exception ex) {

            System.out.println("Error");

        }

    }

    @Override
    public void resetPassword(ResetPasswordRequests request) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (passwordResetToken.isUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        if (passwordResetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = passwordResetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        passwordResetToken.setUsed(true);

        passwordResetTokenRepository.save(passwordResetToken);

    }
}
