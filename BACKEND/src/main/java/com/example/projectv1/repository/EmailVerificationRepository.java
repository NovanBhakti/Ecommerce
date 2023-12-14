package com.example.projectv1.repository;

import com.example.projectv1.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    public EmailVerification findByEmailVerificationToken(String token);
}
