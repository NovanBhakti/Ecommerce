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
@CrossOrigin
@RequiredArgsConstructor
public class ProjectController {

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

    private final UserService userService;

    @GetMapping("/authenticated/home")
    public ResponseEntity<UserResponse> testHome(Authentication authentication) {
        return userService.showUserDetails(authentication);
    }

    @PostMapping("/authenticated/change-password")
    public ResponseEntity<UserResponse> changePassword(@RequestBody ChangePasswordRequest passwordRequest,
                                                       Authentication authentication) {
        String currentPassword = passwordRequest.getCurrentPassword();
        String newPassword = passwordRequest.getNewPassword();

        return userService.changePassword(currentPassword, newPassword, authentication);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponse> forgotPasswordMail(@RequestBody ForgotPasswordRequest forgotPasswordRequest, Authentication authentication) {
        return userService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/authenticated/reset-password")
    public ResponseEntity<UserResponse> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, Authentication authentication){
        return userService.resetPassword(resetPasswordRequest, authentication);
    }



}
