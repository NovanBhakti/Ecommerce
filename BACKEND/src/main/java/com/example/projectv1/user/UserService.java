package com.example.projectv1.user;

import com.example.projectv1.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender javaMailSender;

    public ResponseEntity<UserResponse> showUserDetails(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof User) {
                UserResponse userResponse = new UserResponse(
                        ((User) userDetails).getEmail(),
                        ((User) userDetails).getFirstName(),
                        ((User) userDetails).getLastName(),
                        userDetails.getAuthorities().toString()
                );
                userResponse.setMessage("Authorized");
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserResponse(null, null, null, "User Doesn't Exist"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponse(null, null, null, "Bad Token"));
        }
    }

    public ResponseEntity<UserResponse> changePassword(String currentPassword, String newPassword, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email); // to check if the user is null or not

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);

                UserResponse userResponse = UserResponse.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .message("Password change Successfully")
                        .build();
                return ResponseEntity.ok(userResponse);
            }
        }
        UserResponse errorResponse = UserResponse.builder().message("Failed to change password").build();
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private String generateResetToken(String email) {
        return jwtService.generateResetToken(email);
    }

    private void sendResetEmail(String email, String resetToken) {
        String resetLink = "http://localhost:8080/api/v1/auth/authenticated/change-password?token=" + resetToken;
        String emailBody = "Click the link below to reset your password:\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText(emailBody);

        javaMailSender.send(message);
    }

    public ResponseEntity<UserResponse> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        if (!(userRepository.existsUserByEmail(forgotPasswordRequest.getEmail()))) {
            String message = "Email doesn't exist";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponse.builder().message(message).build());
        }

        String resetToken = generateResetToken(forgotPasswordRequest.getEmail());
        sendResetEmail(forgotPasswordRequest.getEmail(), resetToken);
        String message = "Password reset link sent successfully to the email";

        return ResponseEntity.ok(UserResponse.builder().message(message).build());
    }

}
