package com.example.projectv1.service;


import com.example.projectv1.entity.User;
import com.example.projectv1.entity.UserAddress;
import com.example.projectv1.handler.ProductIdNotFoundException;
import com.example.projectv1.repository.UserAddressRepository;
import com.example.projectv1.repository.UserRepository;
import com.example.projectv1.request.AddressRequest;
import com.example.projectv1.response.AddressResponse;
import com.example.projectv1.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    private User getUserByAuth(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
    }

    @Override
    public ResponseEntity<?> addAddress(Authentication authentication, AddressRequest addressRequest) {
        User user = getUserByAuth(authentication);
        StringBuilder updatedFields = new StringBuilder("Updated fields: ");
        UserAddress address = new UserAddress();
        if (!(addressRequest.getAddressName().isEmpty())) {
            address.setAddressName(addressRequest.getAddressName());
            updatedFields.append("Address name, ");
        }
        if (!(addressRequest.getCountry().isEmpty()) && !(addressRequest.getCountry().equals(address.getCountry()))) {
            address.setCountry(addressRequest.getCountry());
            updatedFields.append("country, ");
        }
        if (!(addressRequest.getState().isEmpty()) && !(addressRequest.getState().equals(address.getState()))) {
            address.setState(addressRequest.getState());
            updatedFields.append("state, ");
        }
        if (!(addressRequest.getCity().isEmpty()) && !(addressRequest.getCity().equals(address.getCity()))) {
            address.setCity(addressRequest.getCity());
            updatedFields.append("city, ");
        }
        if (!(addressRequest.getAddress().isEmpty()) && !(addressRequest.getAddress().equals(address.getAddress()))) {
            address.setAddress(addressRequest.getAddress());
            updatedFields.append("address, ");
        }
        address.setUser(user);
        user.getUserAddress().add(address);
        userRepository.save(user);

        String responseMessage;
        if (updatedFields.length() > "Updated fields: ".length()) {
            responseMessage = updatedFields.substring(0, updatedFields.length() - 2);
        } else {
            responseMessage = "No fields were updated";
        }
        return GlobalResponse
                .responseHandler(responseMessage, HttpStatus.OK, AddressResponse.builder()
                        .id(address.getId())
                        .addressName(address.getAddressName())
                        .country(address.getCountry())
                        .state(address.getState())
                        .city(address.getCity())
                        .address(address.getAddress())
                        .build());
    }

    private UserAddress getUserAddressById(Integer id) {
        Optional<UserAddress> addressOptional = userAddressRepository.findById(id);
        return addressOptional.orElseThrow(() -> new ProductIdNotFoundException("User not found for email: " + id));
    }

    @Override
    public ResponseEntity<?> editAddress(Authentication authentication, AddressRequest addressRequest, Integer id) {
        User user = getUserByAuth(authentication);
        StringBuilder updatedFields = new StringBuilder("Updated fields: ");
        UserAddress address = getUserAddressById(id);
        if (!(addressRequest.getAddressName().isEmpty()) && !addressRequest.getAddressName().equals(address.getAddressName())) {
            address.setAddressName(addressRequest.getAddressName());
            updatedFields.append("Address name, ");
        }
        if (!(addressRequest.getCountry().isEmpty()) && !(addressRequest.getCountry().equals(address.getCountry()))) {
            address.setCountry(addressRequest.getCountry());
            updatedFields.append("country, ");
        }
        if (!(addressRequest.getState().isEmpty()) && !(addressRequest.getState().equals(address.getState()))) {
            address.setState(addressRequest.getState());
            updatedFields.append("state, ");
        }
        if (!(addressRequest.getCity().isEmpty()) && !(addressRequest.getCity().equals(address.getCity()))) {
            address.setCity(addressRequest.getCity());
            updatedFields.append("city, ");
        }
        if (!(addressRequest.getAddress().isEmpty()) && !(addressRequest.getAddress().equals(address.getAddress()))) {
            address.setAddress(addressRequest.getAddress());
            updatedFields.append("address, ");
        }
        userAddressRepository.save(address);

        String responseMessage;
        if (updatedFields.length() > "Updated fields: ".length()) {
            responseMessage = updatedFields.substring(0, updatedFields.length() - 2);
        } else {
            responseMessage = "No fields were updated";
        }
        return GlobalResponse
                .responseHandler(responseMessage, HttpStatus.OK, AddressResponse.builder()
                        .id(address.getId())
                        .addressName(address.getAddressName())
                        .country(address.getCountry())
                        .state(address.getState())
                        .city(address.getCity())
                        .address(address.getAddress())
                        .build());
    }

    @Override
    public ResponseEntity<?> showAllAddress(Authentication authentication) {
        User user = getUserByAuth(authentication);
        List<UserAddress> addresses = userAddressRepository.findUserAddressesByUser(user);
        List<AddressResponse> addressesResponse = new ArrayList<>();

        for (UserAddress address : addresses) {
            AddressResponse addressResponse = AddressResponse.builder()
                    .addressName(address.getAddressName())
                    .country(address.getCountry())
                    .state(address.getState())
                    .city(address.getCity())
                    .address(address.getAddress()).build();
            addressesResponse.add(addressResponse);
        }

        return GlobalResponse.responseHandler("Successfully retrieving addresses", HttpStatus.OK, addressesResponse);

    }

    @Override
    public ResponseEntity<?> removeAddress(Authentication authentication, Integer id) {
        User user = getUserByAuth(authentication);
        UserAddress address = getUserAddressById(id);
        if (user.getUserAddress() != null && !user.getUserAddress().isEmpty()) {
            List<UserAddress> userAddresses = new ArrayList<>(user.getUserAddress());
            for (UserAddress userAddress : userAddresses) {
                userAddress.setUser(null);
                userAddressRepository.delete(userAddress);
            }
            user.getUserAddress().clear();

            userRepository.save(user);

            return GlobalResponse.responseHandler("Address deleted", HttpStatus.OK, null);
        } else {
            return GlobalResponse.responseHandler("There are no address to delete", HttpStatus.OK, null);
        }
    }
}
