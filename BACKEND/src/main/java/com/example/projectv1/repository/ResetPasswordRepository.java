package com.example.projectv1.repository;

import com.example.projectv1.entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    public ResetPassword findByResetPasswordToken(String token);
}
