package com.example.projectv1.service;

import com.example.projectv1.entity.User;
import com.example.projectv1.request.ChangePasswordRequest;
import com.example.projectv1.request.EditProfileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    ResponseEntity<?> userWelcome(Authentication authentication);
    ResponseEntity<?> emailVerification(Authentication authentication);
    ResponseEntity<?> showProfile(Authentication authentication);
    ResponseEntity<?> changePassword(ChangePasswordRequest passwordRequest, Authentication authentication);
    ResponseEntity<?> editProfile(EditProfileRequest editProfileRequest, Authentication authentication);
    ResponseEntity<?> uploadProfilePicture(MultipartFile file, Authentication authentication) throws IOException;
    ResponseEntity<?> showProfilePicture(Authentication authentication);
    ResponseEntity<?> deleteProfilePicture(Authentication authentication);
}
