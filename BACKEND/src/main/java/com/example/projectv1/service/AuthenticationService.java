package com.example.projectv1.service;

import com.example.projectv1.entity.Role;
import com.example.projectv1.entity.User;
import com.example.projectv1.entity.UserRepository;
import com.example.projectv1.request.AuthenticationRequest;
import com.example.projectv1.response.AuthenticationResponse;
import com.example.projectv1.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<AuthenticationResponse> register(RegisterRequest registerRequest) {
        String message;
        if (userRepository.existsUserByEmail(registerRequest.getEmail())) {
            message = "Email is already in use";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder().message(message).build());
        }

        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();

        if (!(user.getEmail().endsWith("@gmail.com"))) {
            message = "Fail";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder().message(message).build());
        }

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        message = "Account Successfully Registered";
        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).message(message).build());
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            var user = userRepository.findByEmail(authenticationRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found")); // Handle user not found

            var jwtToken = jwtService.generateToken(user);

            String message = "User authenticated successfully";
            return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).message(message).build());

        } catch (AuthenticationException e) {
            // Handle authentication failure, e.g., invalid credentials
            String errorMessage = "Authentication failed: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.builder().message(errorMessage).build());
        }
    }

}