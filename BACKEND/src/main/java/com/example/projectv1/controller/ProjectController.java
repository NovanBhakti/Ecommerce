package com.example.projectv1.controller;

import com.example.projectv1.request.*;
import com.example.projectv1.response.AuthenticationResponse;
import com.example.projectv1.response.UserResponse;
import com.example.projectv1.service.AuthenticationService;
import com.example.projectv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ProjectController {

    private final UserService userService;
    private final AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest) {
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return authenticationService.authenticate(authenticationRequest);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponse> forgotPasswordMail(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        return userService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserResponse> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();
        return userService.resetPassword(token, newPassword, confirmPassword);
    }
}
