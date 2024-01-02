package com.example.projectv1.controller;

import com.example.projectv1.request.*;
import com.example.projectv1.service.AuthenticationService;
import com.example.projectv1.service.UserAddressService;
import com.example.projectv1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth/authenticated")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    private final UserAddressService userAddressService;

    @GetMapping("/home")
    public ResponseEntity<?> homePage(Authentication authentication) {
        return userService.userWelcome(authentication);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> showUserProfile(Authentication authentication) {
        return userService.showProfile(authentication);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editProfile(@RequestBody EditProfileRequest editProfileRequest, Authentication authentication) {
        return userService.editProfile(editProfileRequest, authentication);
    }

    @PostMapping("/profile/edit/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest passwordRequest,
                                                       Authentication authentication) {
        return userService.changePassword(passwordRequest, authentication);
    }

    @PostMapping ("/edit/profile-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, Authentication authentication) throws IOException {
        return userService.uploadProfilePicture(file, authentication);
    }

    @GetMapping("/edit/profile-image")
    public ResponseEntity<?> profileImage(Authentication authentication) {
        return userService.showProfilePicture(authentication);
    }

    @DeleteMapping("/edit/profile-image")
    public ResponseEntity<?> deleteProfileImage(Authentication authentication){
        return userService.deleteProfilePicture(authentication);
    }

    @PostMapping("/email-verification")
    public ResponseEntity<?> verifyAccountSendEmail(Authentication authentication) {
        return userService.emailVerification(authentication);
    }

    @PostMapping("/address")
    public ResponseEntity<?> addUserAddress(Authentication authentication, @RequestBody AddressRequest addressRequest){
        return userAddressService.addAddress(authentication, addressRequest);
    }

    @GetMapping("/address")
    public ResponseEntity<?> showUserAddress(Authentication authentication){
        return userAddressService.showAllAddress(authentication);
    }

    @PutMapping("/address")
    public ResponseEntity<?> editUserAdress(Authentication authentication, @RequestBody AddressRequest addressRequest, @RequestParam Integer id){
        return userAddressService.editAddress(authentication, addressRequest, id);
    }

    @DeleteMapping("/address")
    public ResponseEntity<?> deleteUserAddress(Authentication authentication, @RequestParam Integer id){
        return userAddressService.removeAddress(authentication, id);
    }
}

