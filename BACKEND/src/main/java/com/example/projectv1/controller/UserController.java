package com.example.projectv1.controller;

import com.example.projectv1.request.ChangePasswordRequest;
import com.example.projectv1.request.ForgotPasswordRequest;
import com.example.projectv1.request.ResetPasswordRequest;
import com.example.projectv1.response.UserResponse;
import com.example.projectv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/authenticated")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/home")
    public ResponseEntity<UserResponse> testHome(Authentication authentication) {
        return userService.showUserDetails(authentication);
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(@RequestBody ChangePasswordRequest passwordRequest,
                                                       Authentication authentication) {
        String currentPassword = passwordRequest.getCurrentPassword();
        String newPassword = passwordRequest.getNewPassword();

        return userService.changePassword(currentPassword, newPassword, authentication);
    }
}
