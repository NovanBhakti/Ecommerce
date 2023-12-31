package com.example.projectv1.service;

import com.example.projectv1.entity.*;
import com.example.projectv1.repository.EmailVerificationRepository;
import com.example.projectv1.repository.UserRepository;
import com.example.projectv1.request.AuthenticationRequest;
import com.example.projectv1.request.RegisterRequest;
import com.example.projectv1.response.AuthenticationResponse;
import com.example.projectv1.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.projectv1.utils.TemporaryTokenUtil.isResetTokenValid;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final EmailVerificationRepository emailVerificationRepository;
    Object object = new Object();

    public ResponseEntity<?> register(RegisterRequest registerRequest) {
        String message;
        if (userRepository.existsUserByEmail(registerRequest.getEmail())) {
            message = "Email " + registerRequest.getEmail() + " already in use";
            object = AuthenticationResponse.builder().build();
            return GlobalResponse.responseHandler(message, HttpStatus.BAD_REQUEST, object);
        }

        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.NOT_VERIFIED)
                .build();

        if (!(user.getEmail().contains("@") || user.getEmail().contains("."))) {
            message = "Wrong email format!";
            object = AuthenticationResponse.builder().build();
            return GlobalResponse.responseHandler(message, HttpStatus.BAD_REQUEST, object);
        }
        System.out.println(user.getRole());
        userRepository.save(user);
        return emailVerificationOnRegister(registerRequest.getEmail());
    }

    private ResponseEntity<?> emailVerificationOnRegister(String email) {
        String verifyToken = RandomString.make(30);
        String verifyLink = "http://localhost:3000/email-verification?token=" + verifyToken;
        String emailBody = "Click the link below to verify your account:\n" + verifyLink;
        EmailVerification verification;

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            verification = new EmailVerification();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
            verification.setUser(user);
            verification.setEmailVerificationToken(verifyToken);
            verification.setEmailVerificationTokenExpiry(expiryTime);
            user.setEmailVerification(verification);
            userRepository.save(user);
            try {
                emailSenderService.sendEmail(email, "Account Verification", emailBody);
                return GlobalResponse.responseHandler("Account Registered, please check your email to verify your account", HttpStatus.OK, null);
            } catch (MailSendException e){
                return GlobalResponse.responseHandler("Account Registered, failed to send the email address, please verify your account after login!", HttpStatus.OK, null);
            }
        } catch (MailException e) {
            return GlobalResponse.responseHandler("Failed to send mail.", HttpStatus.BAD_REQUEST, null);
        } catch (UsernameNotFoundException e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            var user = userRepository.findByEmail(authenticationRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found")); // Handle user not found

            var jwtToken = jwtServiceImpl.generateToken(user);

            String message = "User authenticated successfully";
            object = AuthenticationResponse.builder().token(jwtToken).role(user.getRole()).build();
            return GlobalResponse.responseHandler(message, HttpStatus.OK, object);
        } catch (AuthenticationException e) {
            // Handle authentication failure, e.g., invalid credentials
            String errorMessage = "Wrong email or password";
            object = AuthenticationResponse.builder().build();
            return GlobalResponse.responseHandler(errorMessage, HttpStatus.UNAUTHORIZED, object);
        }
    }
    private User getByVerificationToken(String token) {
        try {
            return emailVerificationRepository.findByEmailVerificationToken(token).getUser();
        } catch (NullPointerException e) {
            return null;
        }
    }
    public ResponseEntity<?> verifyingEmail(String token) {
        try {
            User user = getByVerificationToken(token);
            assert user != null;
            EmailVerification verification = user.getEmailVerification();
            if (isResetTokenValid(verification.getEmailVerificationTokenExpiry())) {
                user.setRole(Role.USER);
                user.setEmailVerification(null);
                emailVerificationRepository.delete(verification);
                userRepository.save(user);
                var jwtToken = jwtServiceImpl.generateToken(user);
                String message = "User verified";
                object = AuthenticationResponse.builder().token(jwtToken).build();

                return GlobalResponse.responseHandler("Account has verified", HttpStatus.OK, object);
            } else {
                return GlobalResponse.responseHandler("Token Expired", HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            return GlobalResponse.responseHandler("The request is invalid", HttpStatus.BAD_REQUEST, null);
        }
    }
}
