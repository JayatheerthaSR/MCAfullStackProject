package com.app.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setReplyTo("do-not-reply@mail.com");
        message.setFrom("ajaysrao5@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("You have requested a password reset. Please click on the following link to reset your password:\n\n" + resetLink + "\n\nThis link will expire in 2 hours.\n\nIf you did not request this, please ignore this email.");
        
        System.err.println(resetLink);
        
        mailSender.send(message);
        System.out.println("Password reset email sent to: " + toEmail);
    }
}