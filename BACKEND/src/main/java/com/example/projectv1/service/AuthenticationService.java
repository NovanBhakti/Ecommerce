package com.example.projectv1.service;

import com.example.projectv1.request.AuthenticationRequest;
import com.example.projectv1.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(RegisterRequest registerRequest);
    ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest);
}
