package com.example.projectv1.service;

import com.example.projectv1.entity.EmailVerification;
import com.example.projectv1.entity.Role;
import com.example.projectv1.repository.EmailVerificationRepository;
import com.example.projectv1.repository.ProfilePictureRepository;
import com.example.projectv1.entity.ProfilePicture;
import com.example.projectv1.response.GlobalResponse;
import com.example.projectv1.utils.ImageUtils;
import com.example.projectv1.entity.User;
import com.example.projectv1.repository.UserRepository;
import com.example.projectv1.request.ChangePasswordRequest;
import com.example.projectv1.request.EditProfileRequest;
import com.example.projectv1.response.ProfileImageResponse;
import com.example.projectv1.response.ProfileResponse;
import com.example.projectv1.response.UserResponse;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static com.example.projectv1.utils.TemporaryTokenUtil.isResetTokenValid;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageHandlerService imageHandlerService;
    private final EmailSenderService emailSenderService;
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
                                    .role(user.getRole())
                                    .build());
        } catch (Exception e) {
            return GlobalResponse.responseHandler("Bad Token", HttpStatus.UNAUTHORIZED, UserResponse.builder().build());
        }
    }

    public ResponseEntity<?> emailVerification(Authentication authentication){
        String email = authentication.getName();
        String verifyToken = RandomString.make(30);
        String verifyLink = "http://localhost:3000/email-verification?token=" + verifyToken;
        String emailBody = "Click the link below to verify your account:\n" + verifyLink;
        EmailVerification verification;

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            if (user.getEmailVerification() != null && isResetTokenValid(user.getEmailVerification().getEmailVerificationTokenExpiry())) {
                return GlobalResponse.responseHandler("Duplicate email verification request!", HttpStatus.BAD_REQUEST, null);
            } else if (user.getRole().equals(Role.NOT_VERIFIED)){
                if (user.getEmailVerification() != null && !isResetTokenValid(user.getEmailVerification().getEmailVerificationTokenExpiry())){
                    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
                    verification = user.getEmailVerification();
                    verification.setUser(user);
                    verification.setEmailVerificationToken(verifyToken);
                    verification.setEmailVerificationTokenExpiry(expiryTime);
                    user.setEmailVerification(verification);
                    emailSenderService.sendEmail(email, "Account Verification", emailBody);
                    userRepository.save(user);

                    return GlobalResponse.responseHandler("Verification link request has resent", HttpStatus.OK, null);
                } else {
                    verification = new EmailVerification();
                    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(1);
                    verification.setUser(user);
                    verification.setEmailVerificationToken(verifyToken);
                    verification.setEmailVerificationTokenExpiry(expiryTime);
                    user.setEmailVerification(verification);
                    userRepository.save(user);
                    emailSenderService.sendEmail(email, "Account Verification", emailBody);
                    return GlobalResponse.responseHandler("Verification link sent", HttpStatus.OK, null);
                }
            } else {
                return GlobalResponse.responseHandler("Account already verified", HttpStatus.OK, null);
            }
        } catch (MailException e) {
            return GlobalResponse.responseHandler("Failed to send mail.", HttpStatus.BAD_REQUEST, null);
        } catch (UsernameNotFoundException e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    public ResponseEntity<?> showProfile(Authentication authentication) {
        try {
            User user = getUserByAuth(authentication);
            String base64Image = (String) imageHandlerService.rawImageBase64(user);
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
                            .profilePicture(base64Image)
                            .role(user.getRole())
                            .build());
        } catch (Exception e){
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
    private User getUserByAuth(Authentication authentication) {
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
    public ResponseEntity<?> uploadProfilePicture(MultipartFile file, Authentication authentication) throws IOException {
        return imageHandlerService.uploadImage(file, getUserByAuth(authentication));
    }

    @Override
    public ResponseEntity<?> showProfilePicture(Authentication authentication) {
        return imageHandlerService.showImage(getUserByAuth(authentication));
    }

    @Override
    public ResponseEntity<?> deleteProfilePicture(Authentication authentication) {
        return imageHandlerService.deleteImage(getUserByAuth(authentication));
    }
}
