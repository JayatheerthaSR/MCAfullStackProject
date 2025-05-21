package com.app.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private MimeMessageHelper createMimeMessageHelper(MimeMessage message, String toEmail, String subject) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setFrom("${FROM_MAIL}");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        return helper;
    }

    public void sendPasswordResetEmail(String toEmail, String firstName, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Password Reset Request for Banking App");

            String emailContent = String.format("""
                Dear %s,

                You have requested a password reset for your Banking App account.
                To reset your password, please click the link below:

                %s

                This link is valid for a limited time (e.g., 2 hours). If you did not request a password reset, please ignore this email.

                Thank you,
                The Banking App Team

                © 2025 Banking App. All rights reserved.
                """, firstName, resetLink);

            helper.setText(emailContent);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email to " + toEmail + " for user " + firstName + ": " + e.getMessage());
            throw new RuntimeException("Error sending password reset email", e);
        }
    }

    public void sendOTPEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Verify Your Email for Banking App Registration");

            String emailContent = String.format("""
                Dear %s,

                Thank you for registering with Banking App!
                To complete your registration, please use the following One-Time Password (OTP):

                %s

                This OTP is valid for 10 minutes. Do not share this code with anyone.
                If you did not attempt to register, please ignore this email.

                Thank you,
                The Banking App Team

                © 2025 Banking App. All rights reserved.
                """, firstName, otp);

            helper.setText(emailContent);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error sending OTP email", e);
        }
    }

    public void sendUpdateEmailOTPEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Verify Your New Email for Banking App");

            String emailContent = String.format("""
                Dear %s,

                You have requested to update your email address for your Banking App account.
                Please use the following One-Time Password (OTP) to verify your new email:

                %s

                This OTP is valid for 10 minutes. If you did not request this change, please ignore this email.

                Thank you,
                The Banking App Team

                © 2025 Banking App. All rights reserved.
                """, firstName, otp);

            helper.setText(emailContent);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send update email OTP to " + toEmail + " for user " + firstName + ": " + e.getMessage());
            throw new RuntimeException("Error sending update email OTP", e);
        }
    }
}