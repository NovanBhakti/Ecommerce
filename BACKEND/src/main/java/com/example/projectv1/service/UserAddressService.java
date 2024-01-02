package com.example.projectv1.service;

import com.example.projectv1.request.AddressRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UserAddressService {
    ResponseEntity<?> addAddress(Authentication authentication, AddressRequest addressRequest);
    ResponseEntity<?> editAddress(Authentication authentication, AddressRequest addressRequest, Integer id);
    ResponseEntity<?> showAllAddress(Authentication authentication);
    ResponseEntity<?> removeAddress(Authentication authentication, Integer id);
}
