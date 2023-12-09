package com.example.projectv1.service;

import com.example.projectv1.entity.User;
import com.example.projectv1.request.ForgotPasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

public interface ForgotPasswordService {
    ResponseEntity<?> sendResetEmail(String email, String resetToken);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException;
    User getByResetPasswordToken(String token);
    Boolean isResetTokenValid(LocalDateTime expiryTime);
    ResponseEntity<?> resetPassword(String token, String newPassword, String confirmPassword);

}
