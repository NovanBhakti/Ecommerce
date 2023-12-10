package com.example.projectv1.service;

import com.example.projectv1.entity.ProfilePictureRepository;
import com.example.projectv1.entity.ProfilePicture;
import com.example.projectv1.response.GlobalResponse;
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
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfilePictureRepository profilePictureRepository;
    @Override
    public ResponseEntity<?> userWelcome(Authentication authentication) {
        try {
            User user = getUserByAuth(authentication);
            return GlobalResponse
                    .responseHandler("User Authenticated",
                            HttpStatus.OK,
                            UserResponse
                                    .builder()
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .email(user.getEmail())
                                    .build());
        } catch (Exception e) {
            return GlobalResponse.responseHandler("Bad Token", HttpStatus.UNAUTHORIZED, UserResponse.builder().build());
        }
    }
    @Override
    public ResponseEntity<?> showProfile(Authentication authentication) {
        User user = getUserByAuth(authentication);
        return GlobalResponse
                .responseHandler("Successfully retrieving profile data", HttpStatus.OK, ProfileResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dob(user.getDob())
                        .country(user.getCountry())
                        .state(user.getState())
                        .city(user.getCity())
                        .address(user.getAddress())
                        .gender(user.getGender())
                        .build());
    }
    @Override
    public User getUserByAuth(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
    }
    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest passwordRequest, Authentication authentication) {
        User user = getUserByAuth(authentication);

        if (passwordEncoder.matches(passwordRequest.getCurrentPassword(), user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return GlobalResponse
                    .responseHandler("Password change Successfully", HttpStatus.OK, UserResponse.builder()
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build());
        } else {
            return GlobalResponse
                    .responseHandler("Password doesn't match!", HttpStatus.BAD_REQUEST, UserResponse.builder()
                            .build());
        }
    }
    @Override
    public ResponseEntity<?> editProfile(EditProfileRequest editProfileRequest, Authentication authentication) {
        StringBuilder updatedFields = new StringBuilder("Updated fields: ");
        User user = getUserByAuth(authentication);
        LocalDate dob = null;
        Integer age = null;
        try {
          dob = LocalDate.parse(editProfileRequest.getDobString());
        } catch (DateTimeParseException e){
            GlobalResponse.responseHandler(e.getMessage(), HttpStatus.OK, null);
        }
        if (!(editProfileRequest.getFirstName().isBlank()) && !(editProfileRequest.getFirstName().equals(user.getFirstName()))) {
            user.setFirstName(editProfileRequest.getFirstName());
            updatedFields.append("firstName, ");
        }
        if (!(editProfileRequest.getLastName().isEmpty()) && !(editProfileRequest.getLastName().equals(user.getLastName()))) {
            user.setLastName(editProfileRequest.getLastName());
            updatedFields.append("lastName, ");
        }
        if (dob != null && !(dob.equals(user.getDob()))) {
            if(dob.isBefore(LocalDate.now().minusYears(17))){
                user.setDob(dob);
                age = LocalDate.now().getYear() - dob.getYear();
                updatedFields.append("dob, ");
            }
            else {
                updatedFields.append("dob user is under age, ");
            }
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

        return GlobalResponse
                .responseHandler(responseMessage, HttpStatus.OK, ProfileResponse.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .dob(user.getDob())
                        .country(user.getCountry())
                        .state(user.getState())
                        .city(user.getCity())
                        .address(user.getAddress())
                        .gender(user.getGender())
                        .age(age)
                        .build());
    }
    @Override
    public ResponseEntity<?> uploadImage(MultipartFile file, Authentication authentication) throws IOException {
        try {
            User user = getUserByAuth(authentication);
            if (file.isEmpty()) {
                return GlobalResponse
                        .responseHandler("The file is empty", HttpStatus.BAD_REQUEST,
                                ProfileImageResponse
                                        .builder()
                                        .build());
            } else if (!ImageUtils.isImage(file)) {
                return GlobalResponse
                        .responseHandler("Wrong file extension! Only upload PNG, JPG, and JPEG file extensions",
                                HttpStatus.BAD_REQUEST,
                                ProfileImageResponse
                                        .builder()
                                        .build());
            } else {
                if (user.getProfilePicture() != null) {
                    ProfilePicture profilePicture = user.getProfilePicture();
                    profilePicture.setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                    userRepository.save(user);
                    return GlobalResponse.responseHandler("Image updated", HttpStatus.OK, ProfileImageResponse.builder().file(profilePicture.getId()).build());
                }
                else {
                    ProfilePicture profilePicture = new ProfilePicture();
                    profilePicture.setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                    profilePicture.setUser(user);
                    user.setProfilePicture(profilePicture);
                    userRepository.save(user);
                    return GlobalResponse.responseHandler("Image uploaded", HttpStatus.OK, ProfileImageResponse.builder().file(profilePicture.getId()).build());
                }
            }
        } catch (UsernameNotFoundException e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.UNAUTHORIZED, ProfileImageResponse.builder().build());
        }
    }
    @Override
    public ResponseEntity<?> showImage(Authentication authentication) {
        try {
            User user = getUserByAuth(authentication);
            ProfilePicture profilePicture = user.getProfilePicture();
            byte[] images = ImageUtils.imageDecompressor(profilePicture.getProfilePicture());
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(images);
        } catch (NullPointerException e) {
            return GlobalResponse.responseHandler("Profile picture is empty", HttpStatus.OK, null);
        } catch (Exception e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(Authentication authentication) {
        User user = getUserByAuth(authentication);
        if (user.getProfilePicture() != null) {
            ProfilePicture profilePicture = user.getProfilePicture();
            profilePictureRepository.delete(profilePicture);
            user.setProfilePicture(null);
            userRepository.save(user);
            return GlobalResponse.responseHandler("Image deleted", HttpStatus.OK, ProfileImageResponse.builder().build());
        }
        else {
            return GlobalResponse.responseHandler("There's no image", HttpStatus.OK, ProfileImageResponse.builder().build());
        }
    }
}
