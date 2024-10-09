package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetService(UserRepository userRepository, EmailSenderService emailSenderService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        userRepository.save(user);

        emailSenderService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token"));

        if (!user.isPasswordResetTokenValid()) {
            throw new RuntimeException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.clearPasswordResetToken();
        userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}