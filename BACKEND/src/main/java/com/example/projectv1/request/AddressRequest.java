package com.example.projectv1.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String addressName;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String address;
}
