package com.app.banking.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // A utility method to create the base MimeMessageHelper
    private MimeMessageHelper createMimeMessageHelper(MimeMessage message, String toEmail, String subject) throws MessagingException {
        // 'false' for plain text, or simply omit the boolean argument for plain text
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        helper.setFrom("Banking App <bankingappmailer@gmail.com>"); // Your 'from' email address
        helper.setTo(toEmail);
        helper.setSubject(subject);
        return helper;
    }

    // Modified to accept firstName
    public void sendPasswordResetEmail(String toEmail, String firstName, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Password Reset Request for Banking App");

            // Added firstName to the email content
            String emailContent = String.format("""
                Dear %s,

                You have requested a password reset for your Banking App account.
                To reset your password, please click the link below:

                %s

                This link is valid for a limited time (e.g., 2 hours). If you did not request a password reset, please ignore this email.

                Thank you,
                The Banking App Team

                © 2025 Banking App. All rights reserved.
                """, firstName, resetLink); // Added firstName as the first argument

            helper.setText(emailContent); // Set as plain text

            mailSender.send(message);
            System.out.println("Password reset email sent to: " + toEmail + " for user: " + firstName + " with plain text formatting.");

        } catch (MessagingException e) {
            System.err.println("Failed to send password reset email to " + toEmail + " for user " + firstName + ": " + e.getMessage());
            throw new RuntimeException("Error sending password reset email", e);
        }
    }

    // Modified to accept firstName
    public void sendOTPEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Verify Your Email for Banking App Registration");

            // Added firstName to the email content
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
                """, firstName, otp); // Added firstName as the first argument

            helper.setText(emailContent); // Set as plain text

            mailSender.send(message);
            System.out.println("OTP email sent to: " + toEmail + " for user: " + firstName + " with plain text formatting.");

        } catch (MessagingException e) {
            System.err.println("Failed to send OTP email to " + toEmail + " for user " + firstName + ": " + e.getMessage());
            throw new RuntimeException("Error sending OTP email", e);
        }
    }

    // Modified to accept firstName
    public void sendUpdateEmailOTPEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = createMimeMessageHelper(message, toEmail, "Verify Your New Email for Banking App");

            // Added firstName to the email content
            String emailContent = String.format("""
                Dear %s,

                You have requested to update your email address for your Banking App account.
                Please use the following One-Time Password (OTP) to verify your new email:

                %s

                This OTP is valid for 10 minutes. If you did not request this change, please ignore this email.

                Thank you,
                The Banking App Team

                © 2025 Banking App. All rights reserved.
                """, firstName, otp); // Added firstName as the first argument

            helper.setText(emailContent); // Set as plain text

            mailSender.send(message);
            System.out.println("Update email OTP sent to: " + toEmail + " for user: " + firstName + " with plain text formatting.");

        } catch (MessagingException e) {
            System.err.println("Failed to send update email OTP to " + toEmail + " for user " + firstName + ": " + e.getMessage());
            throw new RuntimeException("Error sending update email OTP", e);
        }
    }
}