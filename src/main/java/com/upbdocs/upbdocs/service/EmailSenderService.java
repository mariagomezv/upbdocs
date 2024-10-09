package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.repository.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service

@PropertySource("classpath:application.properties")

public class EmailSenderService implements EmailSender {
    private final JavaMailSender mailSender;
    private String environmentUrl;

    @Autowired
    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${my.url}")
    public void setEnvironmentUrl(String environmentUrl) {
        this.environmentUrl = environmentUrl;
    }

    @Override
    @Async
    public void sendVerificationEmail(String email, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("marojas4126@gmail.com");
        message.setTo(email);
        message.setSubject("Please verify your registration");
        message.setText("To verify your account, please click the link below:\n"
                + environmentUrl + verificationCode);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@yourdomain.com");
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String content = "<p>Hello,</p>"
                    + "<p>You have requested to reset your password.</p>"
                    + "<p>Click the link below to change your password:</p>"
                    + "<p><a href=\"http://localhost:8080/reset-password?token=" + resetToken + "\">Reset Password</a></p>"
                    + "<br>"
                    + "<p>Ignore this email if you do remember your password, "
                    + "or you have not made the request.</p>";

            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email");
        }
    }
}