package com.example.projectv1.service;

import com.example.projectv1.entity.ResetPassword;
import com.example.projectv1.repository.ResetPasswordRepository;
import com.example.projectv1.entity.User;
import com.example.projectv1.repository.UserRepository;
import com.example.projectv1.request.ForgotPasswordRequest;
import com.example.projectv1.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.projectv1.utils.TemporaryTokenUtil.isResetTokenValid;

@Service
@Transactional
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final UserRepository userRepository;
    private final ResetPasswordRepository resetPasswordRepository;
    private final EmailSenderService emailSenderService;

    private ResponseEntity<?> sendResetEmail(String email) {
        String resetToken = RandomString.make(30);
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        String emailBody = "Click the link below to reset your password:\n" + resetLink;
        ResetPassword resetPassword;

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            if (user.getResetPassword() != null && isResetTokenValid(user.getResetPassword().getResetPasswordTokenExpiry())) {
                return GlobalResponse.responseHandler("Duplicate reset password request!", HttpStatus.BAD_REQUEST, null);
            } else if (user.getResetPassword() != null && !isResetTokenValid(user.getResetPassword().getResetPasswordTokenExpiry())){
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
                resetPassword = user.getResetPassword();
                resetPassword.setUser(user);
                resetPassword.setResetPasswordToken(resetToken);
                resetPassword.setResetPasswordTokenExpiry(expiryTime);
                user.setResetPassword(resetPassword);
                emailSenderService.sendEmail(email, "Password Reset", emailBody);
                userRepository.save(user);

                return GlobalResponse.responseHandler("reset password request has resent", HttpStatus.OK, null);
            } else {
                resetPassword = new ResetPassword();
                LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
                resetPassword.setUser(user);
                resetPassword.setResetPasswordToken(resetToken);
                resetPassword.setResetPasswordTokenExpiry(expiryTime);
                user.setResetPassword(resetPassword);
                userRepository.save(user);
                emailSenderService.sendEmail(email, "Password Reset", emailBody);
                return GlobalResponse.responseHandler("Email Sent", HttpStatus.OK, null);
            }
        } catch (MailException e) {
            return GlobalResponse.responseHandler("Failed to send mail.", HttpStatus.BAD_REQUEST, null);
        } catch (UsernameNotFoundException e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        if (!(userRepository.existsUserByEmail(forgotPasswordRequest.getEmail()))) {
            return GlobalResponse.responseHandler("Email doesn't exist", HttpStatus.BAD_REQUEST, null);
        }
        return sendResetEmail(forgotPasswordRequest.getEmail());
    }

    private User getByResetPasswordToken(String token) {
        try {
            return resetPasswordRepository.findByResetPasswordToken(token).getUser();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ResponseEntity<?> resetPassword(String token, String newPassword, String confirmPassword) {
        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User user = getByResetPasswordToken(token);
            assert user != null;
            ResetPassword resetPassword = user.getResetPassword();
            if (newPassword.equals(confirmPassword)) {
                if (isResetTokenValid(resetPassword.getResetPasswordTokenExpiry())) {
                    String encodedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encodedPassword);
                    resetPasswordRepository.delete(resetPassword);
                    user.setResetPassword(null);
                    userRepository.save(user);
                    return GlobalResponse.responseHandler("Password successfully changed", HttpStatus.OK, null);
                } else {
                    return GlobalResponse.responseHandler("Token Expired", HttpStatus.BAD_REQUEST, null);
                }
            } else {
                return GlobalResponse.responseHandler("Passwords do not match!", HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            return GlobalResponse.responseHandler("The request is invalid", HttpStatus.BAD_REQUEST, null);
        }
    }
}
