package com.example.projectv1.auth;

import com.example.projectv1.config.JwtService;
import com.example.projectv1.user.Role;
import com.example.projectv1.user.User;
import com.example.projectv1.user.UserRepository;
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

//    public AuthenticationResponse register(RegisterRequest registerRequest) {
//        String message;
//        var user = User.builder()
//                .firstName(registerRequest.getFirstName())
//                .lastName(registerRequest.getLastName())
//                .email(registerRequest.getEmail())
//                .password(passwordEncoder.encode(registerRequest.getPassword()))
//                .role(Role.USER)
//                .build();
//
//        if (!(user.getEmail().endsWith("@gmail.com"))) {
//            message = "Fail";
//        } else {
//            message = "Success";
//            userRepository.save(user);
//            var jwtToken = jwtService.generateToken(user);
//            return AuthenticationResponse
//                    .builder()
//                    .token(jwtToken)
//                    .message(message)
//                    .build();
//        }
//        return AuthenticationResponse.builder().message(message).build();
//    }

    public ResponseEntity<AuthenticationResponse> register(RegisterRequest registerRequest) {
        String message = "Success";
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
