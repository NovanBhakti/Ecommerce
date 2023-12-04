package com.example.projectv1.service;

import com.example.projectv1.entity.User;
import com.example.projectv1.entity.UserRepository;
import com.example.projectv1.request.ForgotPasswordRequest;
import com.example.projectv1.response.UserResponse;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ForgotPasswordService {
    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;
    private ResponseEntity<UserResponse> sendResetEmail(String email, String resetToken) {
        String resetLink = "http://localhost:8080/api/v1/auth/reset-password?token=" + resetToken;
        String emailBody = "Click the link below to reset your password:\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText(emailBody);
        try {
            javaMailSender.send(message);
            updateResetPasswordToken(resetToken, email);
        } catch (MailException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponse.builder().message("Failed to send mail.").build());
        }
        return ResponseEntity.ok().body(UserResponse.builder().message("Success send email to " + email).build());
    }

    public ResponseEntity<UserResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        if (!(userRepository.existsUserByEmail(forgotPasswordRequest.getEmail()))) {
            String message = "Email doesn't exist";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponse.builder().message(message).build());
        }

        String resetToken = RandomString.make(30);
        sendResetEmail(forgotPasswordRequest.getEmail(), resetToken);
        String message = "Reset password link sent successfully to the email";

        return ResponseEntity.ok(UserResponse.builder().message(message).build());
    }

    public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
        userOptional.ifPresentOrElse(
                user -> {
                    user.setResetPasswordToken(token);
                    user.setResetPasswordTokenExpiry(expiryTime);
                    userRepository.save(user);
                },
                () -> {
                    throw new UsernameNotFoundException("Could not find any user with the email " + email);
                }
        );
    }

    public User getByResetPasswordToken(String token) {
        try{
            User user = userRepository.findByResetPasswordToken(token);
            return user;
        } catch (NullPointerException e){
            return null;
        }
    }

    public Boolean isResetTokenValid(LocalDateTime expiryTime){
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public ResponseEntity<UserResponse> resetPassword(String token, String newPassword, String confirmPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = getByResetPasswordToken(token);

        if (newPassword.equals(confirmPassword) && user != null) {
            if (isResetTokenValid(user.getResetPasswordTokenExpiry())) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
                user.setResetPasswordToken(null);
                user.setResetPasswordTokenExpiry(null);
                userRepository.save(user);
                return ResponseEntity.ok(UserResponse.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .message("Password successfully changed")
                        .build());
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(UserResponse.builder().message("Token Expired").build());
            }
        } else if (!(newPassword.equals(confirmPassword))) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(UserResponse.builder().message("Passwords do not match!").build());
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(UserResponse.builder().message("Invalid token").build());
        }
    }
}
