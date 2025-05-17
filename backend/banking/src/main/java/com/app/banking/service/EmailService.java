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
        message.setFrom("ajaysrao5@gmail.com"); // Replace with your actual email address
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("You have requested a password reset. Please click on the following link to reset your password:\n\n" + resetLink + "\n\nThis link will expire in 2 hours.\n\nIf you did not request this, please ignore this email.");

        System.err.println(resetLink);

        mailSender.send(message);
        System.out.println("Password reset email sent to: " + toEmail);
    }

    public void sendOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo("do-not-reply@mail.com");
        message.setFrom("ajaysrao5@gmail.com"); // Use the same or a different 'from' address as needed
        message.setTo(toEmail);
        message.setSubject("Verify Your Email for Banking App Registration");
        message.setText("Your OTP is: " + otp + ". Please enter this OTP to complete your registration. This OTP will expire in 10 minutes.");

        System.out.println("Sending OTP: " + otp + " to: " + toEmail); // Optional logging

        mailSender.send(message);
        System.out.println("OTP email sent to: " + toEmail);
    }

    public void sendUpdateEmailOTPEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setReplyTo("do-not-reply@mail.com");
        message.setFrom("ajaysrao5@gmail.com"); // Use your email address
        message.setTo(toEmail);
        message.setSubject("Verify Your New Email for Banking App");
        message.setText("You are updating your email address. Please use the following OTP to verify your new email: " + otp + ". This OTP will expire in 10 minutes.");

        System.out.println("Sending update email OTP: " + otp + " to: " + toEmail); // Optional logging

        mailSender.send(message);
        System.out.println("Update email OTP sent to: " + toEmail);
    }
}