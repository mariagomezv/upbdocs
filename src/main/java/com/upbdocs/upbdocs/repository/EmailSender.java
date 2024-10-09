package com.upbdocs.upbdocs.repository;

public interface EmailSender {
    void sendVerificationEmail(String email, String verificationCode);
}
