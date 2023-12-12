package com.example.projectv1.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    public ForgotPassword findByResetPasswordToken(String token);
}
