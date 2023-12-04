package com.example.projectv1.service;

import com.example.projectv1.entity.ProfilePicture;
import com.example.projectv1.utils.ImageUtils;
import com.example.projectv1.entity.User;
import com.example.projectv1.entity.UserRepository;
import com.example.projectv1.request.ChangePasswordRequest;
import com.example.projectv1.request.EditProfileRequest;
import com.example.projectv1.response.ProfileImageResponse;
import com.example.projectv1.response.ProfileResponse;
import com.example.projectv1.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<UserResponse> userWelcome(Authentication authentication) {
        try {
            User user = getUserByAuth(authentication);
            UserResponse userResponse = UserResponse.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .message("User authenticated")
                    .build();
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponse(null, null, null, "Bad Token"));
        }
    }

    public ResponseEntity<ProfileResponse> showProfile(Authentication authentication) {
        User user = getUserByAuth(authentication);
        ProfileResponse profileResponse = ProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .country(user.getCountry())
                .state(user.getState())
                .city(user.getCity())
                .address(user.getAddress())
                .gender(user.getGender())
                .message("Successfully retrieving profile data")
                .build();

        return ResponseEntity.ok(profileResponse);
    }

    public User getUserByAuth(Authentication authentication) {
            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);

            return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
    }

    public ResponseEntity<UserResponse> changePassword(ChangePasswordRequest passwordRequest, Authentication authentication) {
        User user = getUserByAuth(authentication);

        if (passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);

            UserResponse userResponse = UserResponse.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .message("Password change Successfully")
                    .build();
            return ResponseEntity.ok(userResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponse.builder().message("Password doesn't match!").build());
        }
    }

    public ResponseEntity<ProfileResponse> editProfile(EditProfileRequest editProfileRequest, Authentication authentication) {
        StringBuilder updatedFields = new StringBuilder("Updated fields: ");
        User user = getUserByAuth(authentication);

        if (!(editProfileRequest.getFirstName().isBlank()) && !(editProfileRequest.getFirstName().equals(user.getFirstName()))) {
            user.setFirstName(editProfileRequest.getFirstName());
            updatedFields.append("firstName, ");
        }
        if (!(editProfileRequest.getLastName().isEmpty()) && !(editProfileRequest.getLastName().equals(user.getLastName()))) {
            user.setLastName(editProfileRequest.getLastName());
            updatedFields.append("lastName, ");
        }
        if (editProfileRequest.getDob() != null && !(editProfileRequest.getDob().equals(user.getDob())) && editProfileRequest.getDob().isBefore(LocalDate.now().minusYears(17))) {
            user.setDob(editProfileRequest.getDob());
            updatedFields.append("dob, ");
        }
        if (!(editProfileRequest.getCountry().isEmpty()) && !(editProfileRequest.getCountry().equals(user.getCountry()))) {
            user.setCountry(editProfileRequest.getCountry());
            updatedFields.append("country, ");
        }
        if (!(editProfileRequest.getState().isEmpty()) && !(editProfileRequest.getState().equals(user.getState()))) {
            user.setState(editProfileRequest.getState());
            updatedFields.append("state, ");
        }
        if (!(editProfileRequest.getCity().isEmpty()) && !(editProfileRequest.getCity().equals(user.getCity()))) {
            user.setCity(editProfileRequest.getCity());
            updatedFields.append("city, ");
        }
        if (!(editProfileRequest.getAddress().isEmpty()) && !(editProfileRequest.getAddress().equals(user.getAddress()))) {
            user.setAddress(editProfileRequest.getAddress());
            updatedFields.append("address, ");
        }
        if (!(editProfileRequest.getGender().isEmpty()) && !(editProfileRequest.getGender().equals(user.getGender()))) {
            user.setGender(editProfileRequest.getGender());
            updatedFields.append("gender, ");
        }
        userRepository.save(user);

        String responseMessage;
        if (updatedFields.length() > "Updated fields: ".length()) {
            responseMessage = updatedFields.substring(0, updatedFields.length() - 2);
        } else {
            responseMessage = "No fields were updated";
        }

        ProfileResponse profileResponse = ProfileResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDob())
                .country(user.getCountry())
                .state(user.getState())
                .city(user.getCity())
                .address(user.getAddress())
                .gender(user.getGender())
                .message(responseMessage)
                .build();

        return ResponseEntity.ok(profileResponse);
    }

    public ResponseEntity<?> uploadImage(MultipartFile file, Authentication authentication) throws IOException {
        try {
            User user = getUserByAuth(authentication);
            if (file.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ProfileImageResponse.builder()
                                .message("The file is empty")
                                .build());
            }
            else if (!ImageUtils.isImage(file)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ProfileImageResponse.builder()
                                .message("Wrong file extension! Only upload PNG, JPG, and JPEG file extensions")
                                .build());
            } else {
                ProfilePicture profilePicture = new ProfilePicture();
                profilePicture.setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                profilePicture.setUser(user);
                user.setProfilePicture(profilePicture);
                userRepository.save(user);
                return ResponseEntity.ok(ProfileImageResponse.builder().file(profilePicture.getId()).message("Image stored").build());
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ProfileImageResponse.builder().message(e.getMessage()).build());
        }
    }

    public ResponseEntity<?> showImage(Authentication authentication) {
        try {
            User user = getUserByAuth(authentication);
            ProfilePicture profilePicture = user.getProfilePicture();
            byte[] images = ImageUtils.imageDecompressor(profilePicture.getProfilePicture());
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(images);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.OK).body(ProfileImageResponse.builder().message("Profile picture is empty").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ProfileImageResponse.builder().message(e.getMessage()).build());
        }
    }
}
