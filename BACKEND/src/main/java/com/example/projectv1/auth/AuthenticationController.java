package com.example.projectv1.auth;

import com.example.projectv1.user.UserResponse;
import com.example.projectv1.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest){
        return authenticationService.register(registerRequest);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ){
        return authenticationService.authenticate(authenticationRequest);
    }

    private final UserService userService;

    @GetMapping("/home")
    public ResponseEntity<UserResponse> testHome(Authentication authentication) {
        return userService.showUserDetails(authentication);
    }
}
