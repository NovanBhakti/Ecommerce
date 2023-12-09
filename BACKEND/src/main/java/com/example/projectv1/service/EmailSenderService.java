package com.example.projectv1.service;

import org.springframework.mail.javamail.JavaMailSender;

public interface EmailSenderService {
    JavaMailSender javaMailSender();
}
