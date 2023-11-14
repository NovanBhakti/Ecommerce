package com.example.projectv1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<UserResponse> showUserDetails(Authentication authentication){
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof User) {
                UserResponse userResponse = new UserResponse(
                        ((User) userDetails).getEmail(),
                        ((User) userDetails).getFirstName(),
                        ((User) userDetails).getLastName(),
                        userDetails.getAuthorities().toString()
                );
                userResponse.setMessage("Authorized");
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new UserResponse(null, null, null, "User Doesn't Exist"));
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new UserResponse(null, null, null, "Bad Token"));
        }
    }
}
