package com.example.projectv1.service;

import org.springframework.mail.javamail.JavaMailSender;

public interface EmailSenderService {
    void sendEmail(String email, String subject, String emailBody);
}
