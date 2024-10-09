package com.upbdocs.upbdocs.controller;

import com.upbdocs.upbdocs.dto.request.ChangePasswordRequest;
import com.upbdocs.upbdocs.dto.request.LoginRequest;
import com.upbdocs.upbdocs.dto.request.SignupRequest;
import com.upbdocs.upbdocs.dto.response.JwtResponse;
import com.upbdocs.upbdocs.dto.response.MessageResponse;
import com.upbdocs.upbdocs.model.User;
import com.upbdocs.upbdocs.service.AuthService;
import com.upbdocs.upbdocs.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/signup/")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            String result = authService.registerUser(signUpRequest.getUsername(),
                                                     signUpRequest.getEmail(),
                                                     signUpRequest.getPassword());
            return ResponseEntity.ok(new MessageResponse(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

     @PostMapping("/login/")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            String jwt = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/verify/")
    public ResponseEntity<?> verifyUser(@RequestParam("code") String code) {
        try {
            String result = authService.verifyUser(code);
            return ResponseEntity.ok(new MessageResponse(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.initiatePasswordReset(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = ((User) authentication.getPrincipal()).getId();
            passwordResetService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}

